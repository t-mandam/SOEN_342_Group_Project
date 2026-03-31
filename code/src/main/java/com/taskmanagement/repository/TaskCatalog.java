package com.taskmanagement.repository;

import com.taskmanagement.domain.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of TaskRepository that stores tasks in memory
 */
public class TaskCatalog implements TaskRepository {
    private static final TaskCatalog INSTANCE = new TaskCatalog();
    private List<Task> taskCatalog;

    private TaskCatalog() {
        this.taskCatalog = new ArrayList<>();
    }

    public static TaskCatalog getInstance() {
        return INSTANCE;
    }

    @Override
    public void addTask(Task task) {
        if (task != null && !taskCatalog.contains(task)) {
            taskCatalog.add(task);
        }
    }

    @Override
    public void updateTask(Task task) {
        // Find and update the task in the catalog
        for (int i = 0; i < taskCatalog.size(); i++) {
            Task existingTask = taskCatalog.get(i);
            if (existingTask.getId() != null && existingTask.getId().equals(task.getId())) {
                taskCatalog.set(i, task);
                break;
            }
        }
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(taskCatalog);
    }

    /**
     * Removes a task from the catalog
     * @param task the task to remove
     */
    public void removeTask(Task task) {
        taskCatalog.remove(task);
    }

    /**
     * Finds a task by its ID
     * @param id the task ID
     * @return the task if found, null otherwise
     */
    public Task findById(String id) {
        for (Task task : taskCatalog) {
            if (task.getId() != null && task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Returns the number of tasks in the catalog
     * @return the size of the catalog
     */
    public int size() {
        return taskCatalog.size();
    }
}