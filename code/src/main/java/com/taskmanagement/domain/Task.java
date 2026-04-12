package com.taskmanagement.domain;

import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.Status;
import com.taskmanagement.observer.TaskObserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a task in the task management system
 */
public class Task {
    private static final int MAX_SUBTASKS = 20;
    protected String id;
    protected String title;
    protected String description;
    protected Date creationDate;
    protected LocalDate dueDate;
    protected Priority priority;
    protected Status status;
    protected Project project;
    protected List<Subtask> subtasks;
    protected List<Tag> tags;
    protected Recurrence recurrence;
    protected List<TaskObserver> observers;

    public Task() {
        this.subtasks = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.creationDate = new Date();
        this.status = Status.OPEN;
    }

    public Task(String title) {
        this();
        this.title = title;
    }

    // Observer pattern methods
    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (TaskObserver observer : observers) {
            observer.update(this);
        }
    }

    // Business methods
    public boolean completeTask() {
        if (this.status != Status.COMPLETED) {
            this.status = Status.COMPLETED;
            notifyObservers();
            return true;
        }
        return false;
    }

    public boolean cancelTask() {
        if (this.status != Status.CANCELLED) {
            this.status = Status.CANCELLED;
            notifyObservers();
            return true;
        }
        return false;
    }

    public boolean reopenTask() {
        if (this.status != Status.OPEN) {
            this.status = Status.OPEN;
            notifyObservers();
            return true;
        }
        return false;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyObservers();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyObservers();
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        notifyObservers();
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
        notifyObservers();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        notifyObservers();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        notifyObservers();
    }

    public List<Tag> getTags() {
        return tags;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        if (subtasks != null && subtasks.size() > MAX_SUBTASKS) {
            throw new IllegalStateException("A task cannot have more than " + MAX_SUBTASKS + " subtasks.");
        }

        this.subtasks = subtasks == null ? new ArrayList<>() : new ArrayList<>(subtasks);

        for (Subtask subtask : this.subtasks) {
            if (subtask != null && subtask.getParentTask() != this) {
                subtask.setParentTask(this);
            }
        }

        notifyObservers();
    }

    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        if (!this.subtasks.contains(subtask) && this.subtasks.size() >= MAX_SUBTASKS) {
            throw new IllegalStateException("A task cannot have more than " + MAX_SUBTASKS + " subtasks.");
        }

        if (!this.subtasks.contains(subtask)) {
            this.subtasks.add(subtask);
        }

        if (subtask.getParentTask() != this) {
            subtask.setParentTask(this);
        }

        notifyObservers();
    }

    public void removeSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }

        if (this.subtasks.remove(subtask) && subtask.getParentTask() == this) {
            subtask.setParentTask(null);
            notifyObservers();
        }
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyObservers();
    }

    public void addTag(Tag tag) {
        if (!this.tags.contains(tag)) {
            this.tags.add(tag);
            notifyObservers();
        }
    }

    public void removeTag(Tag tag) {
        if (this.tags.remove(tag)) {
            notifyObservers();
        }
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
        notifyObservers();
    }
}