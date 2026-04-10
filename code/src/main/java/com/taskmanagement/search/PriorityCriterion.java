package com.taskmanagement.search;

import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;

/**
 * Search criterion that matches tasks with a specific priority.
 */
public class PriorityCriterion implements SearchCriterion {
    private Priority priority;

    public PriorityCriterion() {
    }

    public PriorityCriterion(Priority priority) {
        this.priority = priority;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null) {
            return false;
        }

        if (priority == null) {
            return true;
        }

        return priority.equals(task.getPriority());
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
