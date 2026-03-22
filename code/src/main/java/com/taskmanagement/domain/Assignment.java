package com.taskmanagement.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an assignment that connects tasks with collaborators
 */
public class Assignment {
    private List<Task> tasks;
    private List<Collaborator> collaborators;

    public Assignment() {
        this.tasks = new ArrayList<>();
        this.collaborators = new ArrayList<>();
    }

    // Methods for managing tasks
    public void addTask(Task task) {
        if (!this.tasks.contains(task)) {
            this.tasks.add(task);
        }
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    // Methods for managing collaborators
    public void addCollaborator(Collaborator collaborator) {
        if (!this.collaborators.contains(collaborator)) {
            this.collaborators.add(collaborator);
        }
    }

    public void removeCollaborator(Collaborator collaborator) {
        this.collaborators.remove(collaborator);
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    // Check if a specific task-collaborator assignment exists
    public boolean hasAssignment(Task task, Collaborator collaborator) {
        return tasks.contains(task) && collaborators.contains(collaborator);
    }
}