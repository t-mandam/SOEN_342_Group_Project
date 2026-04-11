package com.taskmanagement.persistence;

import com.taskmanagement.domain.Assignment;
import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.domain.Project;
import com.taskmanagement.domain.Tag;
import com.taskmanagement.domain.Task;
import com.taskmanagement.persistence.assignment.AssignmentMapper;
import com.taskmanagement.persistence.assignment.AssignmentTableDataGateway;
import com.taskmanagement.persistence.collaborator.CollaboratorMapper;
import com.taskmanagement.persistence.collaborator.CollaboratorTableDataGateway;
import com.taskmanagement.persistence.project.ProjectMapper;
import com.taskmanagement.persistence.project.ProjectTableDataGateway;
import com.taskmanagement.persistence.project_collaborators.ProjectCollaboratorTableDataGateway;
import com.taskmanagement.persistence.tag.TagMapper;
import com.taskmanagement.persistence.tag.TagTableDataGateway;
import com.taskmanagement.persistence.task.TaskMapper;
import com.taskmanagement.persistence.task.TaskTableDataGateway;
import com.taskmanagement.persistence.task_tags.TaskTagTableDataGateway;
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.CollaboratorCatalog;
import com.taskmanagement.repository.ProjectCatalog;
import com.taskmanagement.repository.TagCatalog;
import com.taskmanagement.repository.TaskCatalog;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates loading/saving app state using TDGs and mappers.
 */
public class AppPersistenceManager {
    private final DatabaseConnection databaseConnection;
    private final TaskTableDataGateway taskGateway;
    private final TagTableDataGateway tagGateway;
    private final ProjectTableDataGateway projectGateway;
    private final CollaboratorTableDataGateway collaboratorGateway;
    private final AssignmentTableDataGateway assignmentGateway;
    private final TaskTagTableDataGateway taskTagGateway;
    private final ProjectCollaboratorTableDataGateway projectCollaboratorGateway;

    private final Map<String, String> projectIdsByName;
    private final Map<String, String> tagIdsByName;
    private final Map<String, String> collaboratorIdsByName;

    public AppPersistenceManager() {
        this(DatabaseConnection.getInstance());
    }

    public AppPersistenceManager(DatabaseConnection databaseConnection) {
        this(
                databaseConnection,
                new TaskTableDataGateway(databaseConnection, new TaskMapper()),
                new TagTableDataGateway(databaseConnection, new TagMapper()),
                new ProjectTableDataGateway(databaseConnection, new ProjectMapper()),
                new CollaboratorTableDataGateway(databaseConnection, new CollaboratorMapper()),
                new AssignmentTableDataGateway(databaseConnection, new AssignmentMapper()),
                new TaskTagTableDataGateway(databaseConnection),
                new ProjectCollaboratorTableDataGateway(databaseConnection)
        );
    }

    public AppPersistenceManager(DatabaseConnection databaseConnection,
                                 TaskTableDataGateway taskGateway,
                                 TagTableDataGateway tagGateway,
                                 ProjectTableDataGateway projectGateway,
                                 CollaboratorTableDataGateway collaboratorGateway,
                                 AssignmentTableDataGateway assignmentGateway,
                                 TaskTagTableDataGateway taskTagGateway,
                                 ProjectCollaboratorTableDataGateway projectCollaboratorGateway) {
        this.databaseConnection = databaseConnection;
        this.taskGateway = taskGateway;
        this.tagGateway = tagGateway;
        this.projectGateway = projectGateway;
        this.collaboratorGateway = collaboratorGateway;
        this.assignmentGateway = assignmentGateway;
        this.taskTagGateway = taskTagGateway;
        this.projectCollaboratorGateway = projectCollaboratorGateway;
        this.projectIdsByName = new HashMap<>();
        this.tagIdsByName = new HashMap<>();
        this.collaboratorIdsByName = new HashMap<>();
    }

    public void loadFromDatabase() {
        TaskCatalog taskCatalog = TaskCatalog.getInstance();
        TagCatalog tagCatalog = TagCatalog.getInstance();
        ProjectCatalog projectCatalog = ProjectCatalog.getInstance();
        CollaboratorCatalog collaboratorCatalog = CollaboratorCatalog.getInstance();
        AssignmentCatalog assignmentCatalog = AssignmentCatalog.getInstance();

        taskCatalog.clear();
        tagCatalog.clear();
        projectCatalog.clear();
        collaboratorCatalog.clear();
        assignmentCatalog.clear();

        projectIdsByName.clear();
        tagIdsByName.clear();
        collaboratorIdsByName.clear();

        try {
            Map<String, Project> projectsById = loadProjects(projectCatalog);
            Map<String, Task> tasksById = loadTasks(taskCatalog);
            Map<String, Collaborator> collaboratorsById = loadCollaborators(collaboratorCatalog);

            loadTags(tagCatalog);
            linkTasksToProjects(projectsById, tasksById);
            linkTaskTags(tagCatalog, tasksById);
            linkProjectCollaborators(projectsById, collaboratorsById);
            loadAssignments(assignmentCatalog, tasksById, collaboratorsById);
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load state from database", ex);
        }
    }

    public void saveToDatabase() {
        Connection conn = databaseConnection.getConnection();
        boolean previousAutoCommit;

        try {
            previousAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);

            upsertProjects();
            upsertTags();
            upsertCollaborators();
            upsertTasks();
            refreshTaskTagLinks();
            refreshProjectCollaboratorLinks();
            refreshAssignments();

            conn.commit();
            conn.setAutoCommit(previousAutoCommit);
        } catch (Exception ex) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Failed to rollback transaction after save error", rollbackEx);
            }
            throw new RuntimeException("Failed to save state to database", ex);
        }
    }

    private Map<String, Project> loadProjects(ProjectCatalog projectCatalog) throws SQLException {
        Map<String, Project> projectsById = new HashMap<>();

        for (Project project : projectGateway.findAll()) {
            if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
                continue;
            }

            String normalizedName = project.getName().trim();
            projectCatalog.addProject(project);

            String id = projectGateway.findIdByName(normalizedName);
            if (id != null && !id.trim().isEmpty()) {
                projectsById.put(id, project);
                projectIdsByName.put(normalizedName.toLowerCase(), id);
            }
        }

        return projectsById;
    }

    private Map<String, Task> loadTasks(TaskCatalog taskCatalog) throws SQLException {
        Map<String, Task> tasksById = new HashMap<>();

        for (Task task : taskGateway.findAll()) {
            if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
                continue;
            }

            String taskId = task.getId().trim();
            taskCatalog.addTask(task);
            tasksById.put(taskId, task);
        }

        return tasksById;
    }

    private Map<String, Collaborator> loadCollaborators(CollaboratorCatalog collaboratorCatalog) throws SQLException {
        Map<String, Collaborator> collaboratorsById = new HashMap<>();

        for (Collaborator collaborator : collaboratorGateway.findAll()) {
            if (collaborator == null || collaborator.getName() == null || collaborator.getName().trim().isEmpty()) {
                continue;
            }

            String normalizedName = collaborator.getName().trim();
            collaboratorCatalog.addCollaborator(collaborator);

            String id = collaboratorGateway.findIdByName(normalizedName);
            if (id != null && !id.trim().isEmpty()) {
                collaboratorsById.put(id, collaborator);
                collaboratorIdsByName.put(normalizedName.toLowerCase(), id);
            }
        }

        return collaboratorsById;
    }

    private void loadTags(TagCatalog tagCatalog) throws SQLException {
        for (Tag tag : tagGateway.findAll()) {
            if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                continue;
            }

            String normalizedName = tag.getName().trim();
            tagCatalog.addTag(new Tag(normalizedName));

            String id = tagGateway.findIdByName(normalizedName);
            if (id != null && !id.trim().isEmpty()) {
                tagIdsByName.put(normalizedName.toLowerCase(), id);
            }
        }
    }

    private void linkTasksToProjects(Map<String, Project> projectsById, Map<String, Task> tasksById) throws SQLException {
        for (Map.Entry<String, Project> entry : projectsById.entrySet()) {
            List<Task> projectTasks = taskGateway.findByProjectId(entry.getKey());
            for (Task projectTask : projectTasks) {
                if (projectTask == null || projectTask.getId() == null) {
                    continue;
                }

                Task canonicalTask = tasksById.get(projectTask.getId().trim());
                if (canonicalTask != null) {
                    entry.getValue().addTask(canonicalTask);
                }
            }
        }
    }

    private void linkTaskTags(TagCatalog tagCatalog, Map<String, Task> tasksById) throws SQLException {
        for (Task task : tasksById.values()) {
            if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
                continue;
            }

            for (Tag loadedTag : tagGateway.findByTaskId(task.getId().trim())) {
                if (loadedTag == null || loadedTag.getName() == null || loadedTag.getName().trim().isEmpty()) {
                    continue;
                }

                Tag canonicalTag = tagCatalog.findByName(loadedTag.getName().trim());
                if (canonicalTag != null) {
                    task.addTag(canonicalTag);
                }
            }
        }
    }

    private void linkProjectCollaborators(Map<String, Project> projectsById,
                                          Map<String, Collaborator> collaboratorsById) throws SQLException {
        for (Map.Entry<String, Project> entry : projectsById.entrySet()) {
            for (String collaboratorId : projectCollaboratorGateway.findCollaboratorsByProject(entry.getKey())) {
                Collaborator collaborator = collaboratorsById.get(collaboratorId);
                if (collaborator != null) {
                    entry.getValue().addCollaborator(collaborator);
                }
            }
        }
    }

    private void loadAssignments(AssignmentCatalog assignmentCatalog,
                                 Map<String, Task> tasksById,
                                 Map<String, Collaborator> collaboratorsById) throws SQLException {
        for (Map<String, String> row : assignmentGateway.findAll()) {
            if (row == null) {
                continue;
            }

            Task task = tasksById.get(row.get("task_id"));
            Collaborator collaborator = collaboratorsById.get(row.get("collaborator_id"));
            if (task != null && collaborator != null) {
                assignmentCatalog.addAssignment(new Assignment(task, collaborator));
            }
        }
    }

    private void upsertProjects() throws SQLException {
        for (Project project : ProjectCatalog.getInstance().findAll()) {
            if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
                continue;
            }

            String normalizedName = project.getName().trim();
            String id = projectGateway.findIdByName(normalizedName);
            if (id == null) {
                id = projectGateway.insert(project);
            } else {
                projectGateway.update(id, project);
            }

            projectIdsByName.put(normalizedName.toLowerCase(), id);
        }
    }

    private void upsertTags() throws SQLException {
        for (Tag tag : TagCatalog.getInstance().findAll()) {
            if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                continue;
            }

            String normalizedName = tag.getName().trim();
            String id = tagGateway.findIdByName(normalizedName);
            if (id == null) {
                tagGateway.insert(new Tag(normalizedName));
                id = tagGateway.findIdByName(normalizedName);
            }

            if (id != null && !id.trim().isEmpty()) {
                tagIdsByName.put(normalizedName.toLowerCase(), id);
            }
        }
    }

    private void upsertCollaborators() throws SQLException {
        for (Collaborator collaborator : CollaboratorCatalog.getInstance().findAll()) {
            if (collaborator == null || collaborator.getName() == null || collaborator.getName().trim().isEmpty()) {
                continue;
            }

            String normalizedName = collaborator.getName().trim();
            String id = collaboratorGateway.findIdByName(normalizedName);
            if (id == null) {
                id = collaboratorGateway.insert(collaborator);
            } else {
                collaboratorGateway.update(id, collaborator);
            }

            collaboratorIdsByName.put(normalizedName.toLowerCase(), id);
        }
    }

    private void upsertTasks() throws SQLException {
        for (Task task : TaskCatalog.getInstance().findAll()) {
            if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
                continue;
            }

            String taskId = task.getId().trim();
            if (taskGateway.findById(taskId) == null) {
                taskGateway.insert(task);
            } else {
                taskGateway.update(task);
            }

            String projectId = findProjectIdForTask(task);
            if (projectId != null) {
                taskGateway.assignToProject(taskId, projectId);
            } else {
                taskGateway.removeFromProject(taskId);
            }
        }
    }

    private void refreshTaskTagLinks() throws SQLException {
        for (Task task : TaskCatalog.getInstance().findAll()) {
            if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
                continue;
            }

            String taskId = task.getId().trim();
            taskTagGateway.removeAllTagsFromTask(taskId);

            if (task.getTags() == null) {
                continue;
            }

            for (Tag tag : task.getTags()) {
                if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
                    continue;
                }

                String tagId = tagIdsByName.get(tag.getName().trim().toLowerCase());
                if (tagId != null) {
                    taskTagGateway.addTagToTask(taskId, tagId);
                }
            }
        }
    }

    private void refreshProjectCollaboratorLinks() throws SQLException {
        for (Project project : ProjectCatalog.getInstance().findAll()) {
            if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
                continue;
            }

            String projectId = projectIdsByName.get(project.getName().trim().toLowerCase());
            if (projectId == null) {
                continue;
            }

            projectCollaboratorGateway.removeAllCollaboratorsFromProject(projectId);

            if (project.getCollaborators() == null) {
                continue;
            }

            for (Collaborator collaborator : project.getCollaborators()) {
                if (collaborator == null || collaborator.getName() == null || collaborator.getName().trim().isEmpty()) {
                    continue;
                }

                String collaboratorId = collaboratorIdsByName.get(collaborator.getName().trim().toLowerCase());
                if (collaboratorId != null) {
                    projectCollaboratorGateway.addCollaboratorToProject(projectId, collaboratorId);
                }
            }
        }
    }

    private void refreshAssignments() throws SQLException {
        for (Task task : TaskCatalog.getInstance().findAll()) {
            if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
                continue;
            }

            String taskId = task.getId().trim();
            assignmentGateway.removeByTaskId(taskId);

            for (Assignment assignment : AssignmentCatalog.getInstance().findByTaskId(taskId)) {
                if (assignment == null
                        || assignment.getCollaborator() == null
                        || assignment.getCollaborator().getName() == null
                        || assignment.getCollaborator().getName().trim().isEmpty()) {
                    continue;
                }

                String collaboratorId = collaboratorIdsByName.get(
                        assignment.getCollaborator().getName().trim().toLowerCase()
                );
                if (collaboratorId != null) {
                    assignmentGateway.create(taskId, collaboratorId);
                }
            }
        }
    }

    private String findProjectIdForTask(Task task) {
        if (task == null || task.getProject() == null || task.getProject().getName() == null) {
            return null;
        }

        String projectName = task.getProject().getName().trim();
        if (projectName.isEmpty()) {
            return null;
        }

        return projectIdsByName.get(projectName.toLowerCase());
    }
}
