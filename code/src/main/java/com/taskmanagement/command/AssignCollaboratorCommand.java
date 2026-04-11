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
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.AssignmentRepository;
import com.taskmanagement.repository.CollaboratorCatalog;
import com.taskmanagement.repository.CollaboratorRepository;
import com.taskmanagement.repository.ProjectCatalog;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.repository.TaskRepository;

import java.util.List;

/**
 * Command to assign a collaborator to a task.
 * Automatically creates a linked subtask for the assigned collaborator.
 */
public class AssignCollaboratorCommand implements Command {
    private final AssignmentRepository assignmentRepository;
    private final TaskRepository taskRepository;
    private final CollaboratorRepository collaboratorRepository;
    private final ProjectRepository projectRepository;
    private final TaskFactory taskFactory;
    private final String taskId;
    private final String collaboratorName;

    public AssignCollaboratorCommand() {
        this(AssignmentCatalog.getInstance(), TaskCatalog.getInstance(), CollaboratorCatalog.getInstance(),
             ProjectCatalog.getInstance(),
             new TaskFactory(TaskCatalog.getInstance()), null, null);
    }

    public AssignCollaboratorCommand(String taskId, String collaboratorName) {
        this(AssignmentCatalog.getInstance(), TaskCatalog.getInstance(), CollaboratorCatalog.getInstance(),
             ProjectCatalog.getInstance(),
             new TaskFactory(TaskCatalog.getInstance()), taskId, collaboratorName);
    }

    public AssignCollaboratorCommand(AssignmentRepository assignmentRepository,
                                     TaskRepository taskRepository,
                                     CollaboratorRepository collaboratorRepository,
                                     TaskFactory taskFactory,
                                     String taskId,
                                     String collaboratorName) {
        this(assignmentRepository, taskRepository, collaboratorRepository, ProjectCatalog.getInstance(), taskFactory, taskId, collaboratorName);
    }

    public AssignCollaboratorCommand(AssignmentRepository assignmentRepository,
                                     TaskRepository taskRepository,
                                     CollaboratorRepository collaboratorRepository,
                                     ProjectRepository projectRepository,
                                     TaskFactory taskFactory,
                                     String taskId,
                                     String collaboratorName) {
        this.assignmentRepository = assignmentRepository;
        this.taskRepository = taskRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.projectRepository = projectRepository;
        this.taskFactory = taskFactory;
        this.taskId = taskId;
        this.collaboratorName = collaboratorName;
    }

    @Override
    public void execute() {
        if (assignmentRepository == null) {
            throw new IllegalStateException("Assignment repository cannot be null");
        }
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (collaboratorRepository == null) {
            throw new IllegalStateException("Collaborator repository cannot be null");
        }
        if (projectRepository == null) {
            throw new IllegalStateException("Project repository cannot be null");
        }

        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }
        if (collaboratorName == null || collaboratorName.trim().isEmpty()) {
            throw new IllegalArgumentException("Collaborator name cannot be null or empty");
        }

        Task task = taskRepository.findById(taskId.trim());
        if (task == null) {
            throw new IllegalArgumentException("Task with ID '" + taskId + "' not found");
        }

        Collaborator collaborator = collaboratorRepository.findByName(collaboratorName.trim());
        if (collaborator == null) {
            throw new IllegalArgumentException("Collaborator not found: '" + collaboratorName + "'");
        }

        if (task.getStatus() != Status.OPEN) {
            throw new IllegalArgumentException(
                    "Task '" + task.getId() + "' cannot be assigned because it is not OPEN. Current status: " + task.getStatus()
            );
        }

        if (!isTaskInAnyProject(task.getId())) {
            throw new IllegalArgumentException(
                "Task '" + task.getId() + "' cannot be assigned because it is not part of any project"
            );
        }

        if (hasExistingAssignment(task.getId(), collaborator.getName())) {
            throw new IllegalArgumentException(
                    "Assignment already exists for task '" + task.getId() + "' and collaborator '" + collaborator.getName() + "'"
            );
        }

        int currentOpenTasks = countOpenAssignmentsForCollaborator(collaborator.getName());
        int maxOpenTasks = getMaxOpenTasks(collaborator);
        if (currentOpenTasks >= maxOpenTasks) {
            throw new IllegalArgumentException(
                    "Collaborator '" + collaborator.getName() + "' cannot be assigned more open tasks. Limit: " + maxOpenTasks
            );
        }

        // Automatically create a linked subtask for the assigned collaborator
        String subtaskTitle = collaborator.getName() + " - " + task.getTitle();
        Subtask linkedSubtask = taskFactory.createSubtask(task, subtaskTitle);
        System.out.println("Created linked subtask: " + linkedSubtask.getTitle() + " (ID: " + linkedSubtask.getId() + ")");

        assignmentRepository.addAssignment(new Assignment(task, collaborator));

        Project owningProject = findProjectByTaskId(task.getId());
        if (owningProject != null) {
            owningProject.addCollaborator(collaborator);
            projectRepository.updateProject(owningProject);
        }

        System.out.println("Assigned collaborator '" + collaborator.getName() + "' to task '" + task.getId() + "'.");
    }

    private boolean hasExistingAssignment(String taskId, String collaboratorName) {
        List<Assignment> assignments = assignmentRepository.findByTaskId(taskId);
        for (Assignment assignment : assignments) {
            if (assignment != null
                    && assignment.getCollaborator() != null
                    && assignment.getCollaborator().getName() != null
                    && assignment.getCollaborator().getName().trim().equalsIgnoreCase(collaboratorName.trim())) {
                return true;
            }
        }
        return false;
    }

    private int countOpenAssignmentsForCollaborator(String name) {
        int count = 0;
        List<Assignment> assignments = assignmentRepository.findByCollaboratorName(name);
        for (Assignment assignment : assignments) {
            if (assignment != null && assignment.getTask() != null && assignment.getTask().getStatus() == Status.OPEN) {
                count++;
            }
        }
        return count;
    }

    private int getMaxOpenTasks(Collaborator collaborator) {
        if (collaborator instanceof Junior) {
            return ((Junior) collaborator).getMaxOpenTasks();
        }
        if (collaborator instanceof Intermediate) {
            return ((Intermediate) collaborator).getMaxOpenTasks();
        }
        if (collaborator instanceof Senior) {
            return ((Senior) collaborator).getMaxOpenTasks();
        }

        return Integer.MAX_VALUE;
    }

    private boolean isTaskInAnyProject(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return false;
        }

        String normalizedTaskId = taskId.trim();
        for (Project project : projectRepository.findAll()) {
            if (project == null || project.getTasks() == null) {
                continue;
            }

            for (Task projectTask : project.getTasks()) {
                if (projectTask != null
                        && projectTask.getId() != null
                        && projectTask.getId().trim().equals(normalizedTaskId)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Project findProjectByTaskId(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }

        String normalizedTaskId = taskId.trim();
        for (Project project : projectRepository.findAll()) {
            if (project == null || project.getTasks() == null) {
                continue;
            }

            for (Task projectTask : project.getTasks()) {
                if (projectTask != null
                        && projectTask.getId() != null
                        && projectTask.getId().trim().equals(normalizedTaskId)) {
                    return project;
                }
            }
        }

        return null;
    }

    public AssignmentRepository getAssignmentRepository() {
        return assignmentRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public CollaboratorRepository getCollaboratorRepository() {
        return collaboratorRepository;
    }

    public ProjectRepository getProjectRepository() {
        return projectRepository;
    }

    public String getTaskId() {
        return taskId;
    }

    public TaskFactory getTaskFactory() {
        return taskFactory;
    }

    public String getCollaboratorName() {
        return collaboratorName;
    }
}