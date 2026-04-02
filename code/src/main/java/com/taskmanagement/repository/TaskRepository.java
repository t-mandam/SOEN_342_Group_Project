package com.taskmanagement.repository;

import com.taskmanagement.domain.Task;

import java.util.List;

/**
 * Repository interface for task operations
 */
public interface TaskRepository {
    /**
     * Adds a task to the repository
     * @param task the task to add
     */
    void addTask(Task task);

    /**
     * Updates an existing task in the repository
     * @param task the task to update
     */
    void updateTask(Task task);

    /**
     * Finds and returns all tasks in the repository
     * @return list of all tasks
     */
    List<Task> findAll();

    /**
     * Finds a task by its ID
     * @param id the task ID
     * @return the task if found, null otherwise
     */
    Task findById(String id);
}