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
        this.setParentTask(parentTask);
    }

    // Getters and setters
    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        if (this.parentTask == parentTask) {
            return;
        }

        Task previousParent = this.parentTask;
        this.parentTask = parentTask;

        if (previousParent != null && previousParent.getSubtasks() != null) {
            previousParent.getSubtasks().remove(this);
        }

        if (parentTask != null && parentTask.getSubtasks() != null && !parentTask.getSubtasks().contains(this)) {
            parentTask.getSubtasks().add(this);
        }
    }
}