package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to update a task's title
 */
public class UpdateTitleCommand implements Command {
    private Task task;
    private String newTitle;
    private TaskRepository taskRepository;

    public UpdateTitleCommand() {}

    public UpdateTitleCommand(Task task, String newTitle, TaskRepository taskRepository) {
        this.task = task;
        this.newTitle = newTitle;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute() {
        if (task == null) {
            throw new IllegalStateException("Task cannot be null");
        }
        if (newTitle == null) {
            throw new IllegalStateException("New title cannot be null");
        }
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }

        task.setTitle(newTitle);
        taskRepository.updateTask(task);
    }

    // Getters and setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getNewTitle() {
        return newTitle;
    }

    public void setNewTitle(String newTitle) {
        this.newTitle = newTitle;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}