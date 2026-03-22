package com.taskmanagement.domain;

/**
 * Represents a subtask that inherits from Task
 */
public class Subtask extends Task {
    private Task parentTask;

    public Subtask() {
        super();
    }

    public Subtask(String title) {
        super(title);
    }

    public Subtask(String title, Task parentTask) {
        super(title);
        this.parentTask = parentTask;
    }

    // Getters and setters
    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }
}