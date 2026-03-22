package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Command to update a task's due date
 */
public class UpdateDueDateCommand implements Command {
    private Task task;
    private LocalDateTime newDueDate;
    private TaskRepository taskRepository;

    public UpdateDueDateCommand() {}

    public UpdateDueDateCommand(Task task, LocalDateTime newDueDate, TaskRepository taskRepository) {
        this.task = task;
        this.newDueDate = newDueDate;
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

        // Convert LocalDateTime to Date for compatibility with the Task class
        Date dueDate = newDueDate != null
            ? Date.from(newDueDate.atZone(ZoneId.systemDefault()).toInstant())
            : null;

        task.setDueDate(dueDate);
        taskRepository.updateTask(task);
    }

    // Getters and setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalDateTime getNewDueDate() {
        return newDueDate;
    }

    public void setNewDueDate(LocalDateTime newDueDate) {
        this.newDueDate = newDueDate;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}