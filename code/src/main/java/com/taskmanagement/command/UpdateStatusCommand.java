package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Status;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to update a task's status
 */
public class UpdateStatusCommand implements Command {
    private Task task;
    private Status newStatus;
    private TaskRepository taskRepository;

    public UpdateStatusCommand() {}

    public UpdateStatusCommand(Task task, Status newStatus, TaskRepository taskRepository) {
        this.task = task;
        this.newStatus = newStatus;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute() {
        if (task == null) {
            throw new IllegalStateException("Task cannot be null");
        }
        if (newStatus == null) {
            throw new IllegalStateException("New status cannot be null");
        }
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }

        task.setStatus(newStatus);
        taskRepository.updateTask(task);
    }

    /**
     * Convenience method to complete the task
     */
    public void completeTask() {
        if (task != null) {
            task.completeTask();
            if (taskRepository != null) {
                taskRepository.updateTask(task);
            }
        }
    }

    /**
     * Convenience method to cancel the task
     */
    public void cancelTask() {
        if (task != null) {
            task.cancelTask();
            if (taskRepository != null) {
                taskRepository.updateTask(task);
            }
        }
    }

    /**
     * Convenience method to reopen the task
     */
    public void reopenTask() {
        if (task != null) {
            task.reopenTask();
            if (taskRepository != null) {
                taskRepository.updateTask(task);
            }
        }
    }

    // Getters and setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Status newStatus) {
        this.newStatus = newStatus;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}