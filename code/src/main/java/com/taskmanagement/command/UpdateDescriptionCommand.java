package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to update a task's description
 */
public class UpdateDescriptionCommand implements Command {
    private Task task;
    private String newDescription;
    private TaskRepository taskRepository;

    public UpdateDescriptionCommand() {}

    public UpdateDescriptionCommand(Task task, String newDescription, TaskRepository taskRepository) {
        this.task = task;
        this.newDescription = newDescription;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute() {
        if (task == null) {
            throw new IllegalStateException("Task cannot be null");
        }
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }

        task.setDescription(newDescription);
        taskRepository.updateTask(task);
    }

    // Getters and setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public void setNewDescription(String newDescription) {
        this.newDescription = newDescription;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}