package com.taskmanagement.search;

import com.taskmanagement.domain.Task;

/**
 * Interface for search criteria that can match tasks
 */
public interface SearchCriterion {
    /**
     * Checks if a task matches this criterion
     * @param task the task to check
     * @return true if the task matches, false otherwise
     */
    boolean matches(Task task);
}