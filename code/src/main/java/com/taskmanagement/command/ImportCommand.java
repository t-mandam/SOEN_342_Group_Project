package com.taskmanagement.command;

import com.taskmanagement.domain.Assignment;
import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.domain.Intermediate;
import com.taskmanagement.domain.Junior;
import com.taskmanagement.domain.Project;
import com.taskmanagement.domain.Senior;
import com.taskmanagement.domain.Subtask;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Status;
import com.taskmanagement.factory.TaskFactory;
import com.taskmanagement.observer.Activity;
import com.taskmanagement.observer.ActivityRecorder;
import com.taskmanagement.persistence.DatabaseConnection;
import com.taskmanagement.persistence.activity.DatabaseActivityRecorder;
import com.taskmanagement.repository.AssignmentRepository;
import com.taskmanagement.repository.CollaboratorRepository;
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.CollaboratorCatalog;
import com.taskmanagement.repository.ProjectCatalog;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.repository.TaskRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Command to import tasks from an external source
 */
public class ImportCommand implements Command {
    private static final int MAX_OPEN_WITHOUT_DUE_DATE = 50;
    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private CollaboratorRepository collaboratorRepository;
    private AssignmentRepository assignmentRepository;
    private TaskFactory taskFactory;
    private ActivityRecorder activityRecorder;
    private String importSource;
    private ImportData importData;

    public ImportCommand() {
        this.importData = new ImportData();
        this.taskRepository = TaskCatalog.getInstance();
        this.projectRepository = ProjectCatalog.getInstance();
        this.collaboratorRepository = CollaboratorCatalog.getInstance();
        this.assignmentRepository = AssignmentCatalog.getInstance();
        this.taskFactory = new TaskFactory(this.taskRepository);
        this.activityRecorder = new DatabaseActivityRecorder(DatabaseConnection.getInstance());
    }

    public ImportCommand(String importSource) {
        this();
        this.importSource = importSource;
    }

    public ImportCommand(String importSource, ImportData importData) {
        this(importSource);
        this.importData = importData != null ? importData : new ImportData();
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (importSource == null || importSource.trim().isEmpty()) {
            throw new IllegalStateException("Import source cannot be null or empty");
        }
        if (activityRecorder == null) {
            throw new IllegalStateException("Activity recorder cannot be null");
        }

        try {
            List<ImportData.ImportedTaskData> importedRows = importData.readTasksFromCsv(importSource);
            int importedCount = 0;
            int skippedCount = 0;

            for (ImportData.ImportedTaskData importedRow : importedRows) {
                if (taskAlreadyExists(importedRow)) {
                    skippedCount++;
                    continue;
                }

                Task task = importedRow.getTask();
                if (task.getId() == null || task.getId().trim().isEmpty()) {
                    task.setId(nextTaskId());
                }

                if (task.getProject() != null && projectRepository != null) {
                    Project project = resolveOrCreateProject(task.getProject());
                    project.addTask(task);
                    projectRepository.updateProject(project);
                }

                ensureCanCreateOpenWithoutDueDate(task);
                taskRepository.addTask(task);
                recordActivity(task, "Task " + task.getId() + " created via import from '" + importSource + "'");

                if (importedRow.getSubtaskTitle() != null && !importedRow.getSubtaskTitle().trim().isEmpty()) {
                    ensureOpenWithoutDueDateCapacityForAnotherTask();
                    Subtask createdSubtask = taskFactory.createSubtask(task, importedRow.getSubtaskTitle().trim());
                    recordActivity(createdSubtask, "Subtask " + createdSubtask.getId() + " created via import under task " + task.getId());
                }

                if (importedRow.getCollaboratorName() != null && !importedRow.getCollaboratorName().trim().isEmpty()) {
                    Collaborator collaborator = resolveOrCreateCollaborator(
                            importedRow.getCollaboratorName(),
                            importedRow.getCollaboratorCategory()
                    );

                    if (task.getProject() != null && projectRepository != null) {
                        Project project = resolveOrCreateProject(task.getProject());
                        project.addCollaborator(collaborator);
                        projectRepository.updateProject(project);
                    }

                    if (assignmentRepository != null && task.getStatus() == com.taskmanagement.enums.Status.OPEN) {
                        assignmentRepository.addAssignment(new Assignment(task, collaborator));
                    }
                }

                importedCount++;
            }

            System.out.println(importedCount + " task(s) imported from: " + importSource);
            if (skippedCount > 0) {
                System.out.println(skippedCount + " duplicate task(s) skipped.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to import tasks from: " + importSource, e);
        }
    }

    private boolean taskAlreadyExists(ImportData.ImportedTaskData importedRow) {
        if (importedRow == null || importedRow.getTask() == null) {
            return false;
        }

        for (Task existingTask : taskRepository.findAll()) {
            if (existingTask == null || existingTask instanceof Subtask) {
                continue;
            }

            if (!matchesCoreColumns(existingTask, importedRow)) {
                continue;
            }
            if (!matchesProjectColumns(existingTask, importedRow)) {
                continue;
            }
            if (!matchesSubtaskColumn(existingTask, importedRow.getSubtaskTitle())) {
                continue;
            }
            if (!matchesCollaboratorColumns(existingTask, importedRow.getCollaboratorName(), importedRow.getCollaboratorCategory())) {
                continue;
            }

            return true;
        }

        return false;
    }

    private boolean matchesCoreColumns(Task existingTask, ImportData.ImportedTaskData importedRow) {
        Task importedTask = importedRow.getTask();

        if (!sameText(existingTask.getTitle(), importedTask.getTitle())) {
            return false;
        }
        if (!sameText(existingTask.getDescription(), importedTask.getDescription())) {
            return false;
        }
        if (!sameEnum(existingTask.getStatus(), importedTask.getStatus())) {
            return false;
        }
        if (!sameEnum(existingTask.getPriority(), importedTask.getPriority())) {
            return false;
        }

        LocalDate existingDueDate = existingTask.getDueDate();
        LocalDate importedDueDate = importedTask.getDueDate();
        return Objects.equals(existingDueDate, importedDueDate);
    }

    private boolean matchesProjectColumns(Task existingTask, ImportData.ImportedTaskData importedRow) {
        Task importedTask = importedRow.getTask();
        Project existingProject = existingTask.getProject();
        Project importedProject = importedTask.getProject();

        if (importedProject == null) {
            return existingProject == null;
        }
        if (existingProject == null) {
            return false;
        }

        return sameText(existingProject.getName(), importedProject.getName())
                && sameText(existingProject.getDescription(), importedProject.getDescription());
    }

    private boolean matchesSubtaskColumn(Task existingTask, String importedSubtaskTitle) {
        String normalizedImportedSubtaskTitle = normalize(importedSubtaskTitle);

        if (normalizedImportedSubtaskTitle == null) {
            return existingTask.getSubtasks() == null || existingTask.getSubtasks().isEmpty();
        }

        if (existingTask.getSubtasks() == null || existingTask.getSubtasks().isEmpty()) {
            return false;
        }

        for (Subtask subtask : existingTask.getSubtasks()) {
            if (subtask != null && sameText(subtask.getTitle(), normalizedImportedSubtaskTitle)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesCollaboratorColumns(Task existingTask,
                                               String importedCollaboratorName,
                                               String importedCollaboratorCategory) {
        String normalizedName = normalize(importedCollaboratorName);
        String normalizedCategory = normalize(importedCollaboratorCategory);

        if (normalizedName == null && normalizedCategory == null) {
            return assignmentRepository == null || assignmentRepository.findByTaskId(existingTask.getId()).isEmpty();
        }

        if (assignmentRepository == null) {
            return false;
        }

        for (Assignment assignment : assignmentRepository.findByTaskId(existingTask.getId())) {
            if (assignment == null || assignment.getCollaborator() == null) {
                continue;
            }

            Collaborator collaborator = assignment.getCollaborator();
            if (!sameText(collaborator.getName(), normalizedName)) {
                continue;
            }

            String existingCategory = collaborator.getClass().getSimpleName().toLowerCase(Locale.ROOT);
            if (normalizedCategory == null || existingCategory.equals(normalizedCategory)) {
                return true;
            }
        }

        return false;
    }

    private boolean sameEnum(Enum<?> first, Enum<?> second) {
        return Objects.equals(first, second);
    }

    private boolean sameText(String first, String second) {
        return Objects.equals(normalize(first), normalize(second));
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String nextTaskId() {
        long maxTaskId = 0L;

        for (Task existingTask : taskRepository.findAll()) {
            if (existingTask == null || existingTask.getId() == null || existingTask.getId().trim().isEmpty()) {
                continue;
            }

            try {
                long parsedId = Long.parseLong(existingTask.getId().trim());
                if (parsedId > maxTaskId) {
                    maxTaskId = parsedId;
                }
            } catch (NumberFormatException ignored) {
                // Ignore non-numeric task IDs while finding the next numeric task ID.
            }
        }

        return String.valueOf(maxTaskId + 1);
    }

    private Project resolveOrCreateProject(Project importedProject) {
        if (importedProject == null || importedProject.getName() == null || importedProject.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Imported task project name cannot be null or empty");
        }

        String projectName = importedProject.getName().trim();
        Project existingProject = findProjectByName(projectName);
        if (existingProject != null) {
            return existingProject;
        }

        Project newProject = new Project(projectName, importedProject.getDescription());
        projectRepository.addProject(newProject);
        return newProject;
    }

    private Collaborator resolveOrCreateCollaborator(String collaboratorName, String collaboratorCategory) {
        if (collaboratorRepository == null) {
            throw new IllegalStateException("Collaborator repository cannot be null when collaborator data is present");
        }

        String normalizedName = collaboratorName.trim();
        Collaborator existingCollaborator = collaboratorRepository.findByName(normalizedName);
        if (existingCollaborator != null) {
            return existingCollaborator;
        }

        Collaborator collaborator = createCollaborator(collaboratorCategory, normalizedName);
        collaboratorRepository.addCollaborator(collaborator);
        return collaborator;
    }

    private Collaborator createCollaborator(String category, String name) {
        String normalizedCategory = category == null ? "" : category.trim().toLowerCase();
        switch (normalizedCategory) {
            case "junior":
                return new Junior(name);
            case "intermediate":
                return new Intermediate(name);
            case "senior":
                return new Senior(name);
            case "":
                return new Junior(name);
            default:
                throw new IllegalArgumentException("Invalid collaborator category. Valid values: junior, intermediate, senior");
        }
    }

    private Project findProjectByName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty() || projectRepository == null) {
            return null;
        }

        String normalizedProjectName = projectName.trim();
        for (Project project : projectRepository.findAll()) {
            if (project == null || project.getName() == null) {
                continue;
            }

            if (project.getName().trim().equalsIgnoreCase(normalizedProjectName)) {
                return project;
            }
        }

        return null;
    }

    // Getters and setters
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        this.taskFactory = taskRepository == null ? null : new TaskFactory(taskRepository);
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public CollaboratorRepository getCollaboratorRepository() {
        return collaboratorRepository;
    }

    public void setCollaboratorRepository(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    public AssignmentRepository getAssignmentRepository() {
        return assignmentRepository;
    }

    public void setAssignmentRepository(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public void setTaskFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    public ActivityRecorder getActivityRecorder() {
        return activityRecorder;
    }

    public void setActivityRecorder(ActivityRecorder activityRecorder) {
        this.activityRecorder = activityRecorder;
    }

    public String getImportSource() {
        return importSource;
    }

    public void setImportSource(String importSource) {
        this.importSource = importSource;
    }

    public ImportData getImportData() {
        return importData;
    }

    public void setImportData(ImportData importData) {
        this.importData = importData;
    }

    private void recordActivity(Task task, String description) {
        if (task == null) {
            return;
        }

        Activity activity = new Activity(description);
        activity.setTaskId(task.getId());
        activityRecorder.record(activity);
    }

    private void ensureCanCreateOpenWithoutDueDate(Task task) {
        if (task == null) {
            return;
        }

        if (task.getStatus() == Status.OPEN && task.getDueDate() == null) {
            ensureOpenWithoutDueDateCapacityForAnotherTask();
        }
    }

    private void ensureOpenWithoutDueDateCapacityForAnotherTask() {
        int openWithoutDueDateCount = 0;
        for (Task existingTask : taskRepository.findAll()) {
            if (existingTask != null
                    && existingTask.getStatus() == Status.OPEN
                    && existingTask.getDueDate() == null) {
                openWithoutDueDateCount++;
            }
        }

        if (openWithoutDueDateCount >= MAX_OPEN_WITHOUT_DUE_DATE) {
            throw new IllegalStateException(
                    "The number of OPEN tasks without a due date cannot exceed " + MAX_OPEN_WITHOUT_DUE_DATE + "."
            );
        }
    }
}
