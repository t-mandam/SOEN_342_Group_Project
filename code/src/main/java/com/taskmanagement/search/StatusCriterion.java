package com.taskmanagement.search;

import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Status;

/**
 * Search criterion that matches tasks with a specific status
 */
public class StatusCriterion implements SearchCriterion {
    private Status status;

    public StatusCriterion() {}

    public StatusCriterion(Status status) {
        this.status = status;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null) {
            return false;
        }

        // If no status is specified, match all tasks
        if (status == null) {
            return true;
        }

        return status.equals(task.getStatus());
    }

    // Getters and setters
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}