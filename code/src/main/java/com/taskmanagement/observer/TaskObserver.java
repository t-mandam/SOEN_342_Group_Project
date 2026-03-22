package com.taskmanagement.observer;

import com.taskmanagement.domain.Task;

/**
 * Observer interface for task updates
 */
public interface TaskObserver {
    /**
     * Called when a task is updated
     * @param task the updated task
     */
    void update(Task task);
}