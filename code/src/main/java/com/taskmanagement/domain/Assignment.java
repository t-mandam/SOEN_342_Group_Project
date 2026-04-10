package com.taskmanagement.domain;

/**
 * Represents a single assignment between one task and one collaborator.
 */
public class Assignment {
    private Task task;
    private Collaborator collaborator;

    public Assignment() {
    }

    public Assignment(Task task, Collaborator collaborator) {
        this.task = task;
        this.collaborator = collaborator;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    // Checks if this assignment matches the given task and collaborator pair.
    public boolean hasAssignment(Task task, Collaborator collaborator) {
        return this.task != null
                && this.collaborator != null
                && this.task.equals(task)
                && this.collaborator.equals(collaborator);
    }
}