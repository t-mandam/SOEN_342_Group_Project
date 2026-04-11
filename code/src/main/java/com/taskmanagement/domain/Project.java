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
        if (this.tasks != null) {
            for (Task task : this.tasks) {
                if (task != null && sameProject(task.getProject(), this)) {
                    task.setProject(null);
                }
            }
        }

        this.tasks = tasks;

        if (this.tasks != null) {
            for (Task task : this.tasks) {
                if (task != null) {
                    task.setProject(this);
                }
            }
        }
    }

    public void addTask(Task task) {
        if (task == null) {
            return;
        }

        if (task.getProject() != null && !sameProject(task.getProject(), this)) {
            throw new IllegalArgumentException("Task '" + task.getId() + "' is already part of project '" + task.getProject().getName() + "'");
        }

        if (!this.tasks.contains(task)) {
            this.tasks.add(task);
        }

        task.setProject(this);
    }

    public void removeTask(Task task) {
        if (task == null) {
            return;
        }

        if (this.tasks.remove(task) && sameProject(task.getProject(), this)) {
            task.setProject(null);
        }
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

    private boolean sameProject(Project first, Project second) {
        if (first == null || second == null) {
            return false;
        }

        if (first.getName() == null || second.getName() == null) {
            return false;
        }

        return first.getName().trim().equalsIgnoreCase(second.getName().trim());
    }
}