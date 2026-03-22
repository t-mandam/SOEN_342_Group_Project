package com.taskmanagement.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a project that contains tasks and collaborators
 */
public class Project {
    private String name;
    private String description;
    private List<Task> tasks;
    private List<Collaborator> collaborators;

    public Project() {
        this.tasks = new ArrayList<>();
        this.collaborators = new ArrayList<>();
    }

    public Project(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        if (!this.tasks.contains(task)) {
            this.tasks.add(task);
        }
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public void addCollaborator(Collaborator collaborator) {
        if (!this.collaborators.contains(collaborator)) {
            this.collaborators.add(collaborator);
        }
    }

    public void removeCollaborator(Collaborator collaborator) {
        this.collaborators.remove(collaborator);
    }
}