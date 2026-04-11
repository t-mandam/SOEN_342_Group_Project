package com.taskmanagement.command;

import com.taskmanagement.domain.Project;
import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.ProjectCatalog;
import com.taskmanagement.repository.ProjectRepository;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to add an existing task to an existing project.
 */
public class AddTaskToProjectCommand implements Command {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final String taskId;
    private final String projectName;

    public AddTaskToProjectCommand(String taskId, String projectName) {
        this(TaskCatalog.getInstance(), ProjectCatalog.getInstance(), taskId, projectName);
    }

    public AddTaskToProjectCommand(TaskRepository taskRepository,
                                   ProjectRepository projectRepository,
                                   String taskId,
                                   String projectName) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskId = taskId;
        this.projectName = projectName;
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (projectRepository == null) {
            throw new IllegalStateException("Project repository cannot be null");
        }
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be null or empty");
        }
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }

        Task task = taskRepository.findById(taskId.trim());
        if (task == null) {
            throw new IllegalArgumentException("Task with ID '" + taskId + "' not found");
        }

        Project project = projectRepository.findByName(projectName.trim());
        if (project == null) {
            throw new IllegalArgumentException("Project not found: '" + projectName + "'");
        }

        Project existingProject = findProjectContainingTask(task);
        if (existingProject != null) {
            if (existingProject.getName() != null
                && existingProject.getName().trim().equalsIgnoreCase(project.getName().trim())) {
            throw new IllegalArgumentException(
                "Task '" + task.getId() + "' is already part of project '" + project.getName() + "'"
            );
            }

            throw new IllegalArgumentException(
                "Task '" + task.getId() + "' is already assigned to project '" + existingProject.getName() + "'. "
                    + "A task can belong to only one project."
            );
        }

        if (project.getTasks() != null && project.getTasks().contains(task)) {
            throw new IllegalArgumentException(
                    "Task '" + task.getId() + "' is already part of project '" + project.getName() + "'"
            );
        }

        project.addTask(task);
        projectRepository.updateProject(project);

        System.out.println("Task '" + task.getId() + "' added to project '" + project.getName() + "'.");
    }

    private Project findProjectContainingTask(Task task) {
        if (task == null) {
            return null;
        }

        if (task.getProject() != null) {
            Project attachedProject = projectRepository.findByName(task.getProject().getName());
            return attachedProject != null ? attachedProject : task.getProject();
        }

        String normalizedTaskId = task.getId();
        if (normalizedTaskId == null || normalizedTaskId.trim().isEmpty()) {
            return null;
        }

        normalizedTaskId = normalizedTaskId.trim();
        for (Project candidate : projectRepository.findAll()) {
            if (candidate == null || candidate.getTasks() == null) {
                continue;
            }

            for (Task candidateTask : candidate.getTasks()) {
                if (candidateTask != null
                        && candidateTask.getId() != null
                        && candidateTask.getId().trim().equals(normalizedTaskId)) {
                    return candidate;
                }
            }
        }

        return null;
    }
}
