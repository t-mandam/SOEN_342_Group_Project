package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to update a task's priority
 */
public class UpdatePriorityCommand implements Command {
    private Task task;
    private Priority newPriority;
    private TaskRepository taskRepository;

    public UpdatePriorityCommand() {}

    public UpdatePriorityCommand(Task task, Priority newPriority, TaskRepository taskRepository) {
        this.task = task;
        this.newPriority = newPriority;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute() {
        if (task == null) {
            throw new IllegalStateException("Task cannot be null");
        }
        if (newPriority == null) {
            throw new IllegalStateException("New priority cannot be null");
        }
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }

        task.setPriority(newPriority);
        taskRepository.updateTask(task);
    }

    // Getters and setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Priority getNewPriority() {
        return newPriority;
    }

    public void setNewPriority(Priority newPriority) {
        this.newPriority = newPriority;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}