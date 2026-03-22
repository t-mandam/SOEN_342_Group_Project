package com.taskmanagement.factory;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Subtask;
import com.taskmanagement.domain.Recurrence;
import com.taskmanagement.repository.TaskRepository;

import java.util.UUID;

/**
 * Factory class for creating different types of tasks
 */
public class TaskFactory {
    private TaskRepository taskRepository;

    public TaskFactory() {}

    public TaskFactory(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Creates a basic task with the given title
     * @param title the title of the task
     * @return the created task
     */
    public Task createTask(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }

        Task task = new Task(title.trim());
        task.setId(generateUniqueId());

        // Add to repository if available
        if (taskRepository != null) {
            taskRepository.addTask(task);
        }

        return task;
    }

    /**
     * Creates a recurring task with the given title and recurrence
     * @param title the title of the task
     * @param recurrence the recurrence pattern
     * @return the created recurring task
     */
    public Task createRecurringTask(String title, Recurrence recurrence) {
        Task task = createTask(title);
        task.setRecurrence(recurrence);

        // Update in repository if available
        if (taskRepository != null) {
            taskRepository.updateTask(task);
        }

        return task;
    }

    /**
     * Creates a subtask with the given parent task
     * @param parentTask the parent task
     * @return the created subtask
     */
    public Subtask createSubtask(Task parentTask) {
        if (parentTask == null) {
            throw new IllegalArgumentException("Parent task cannot be null");
        }

        String subtaskTitle = "Subtask of: " + parentTask.getTitle();
        return createSubtask(parentTask, subtaskTitle);
    }

    /**
     * Creates a subtask with the given parent task and title
     * @param parentTask the parent task
     * @param title the title for the subtask
     * @return the created subtask
     */
    public Subtask createSubtask(Task parentTask, String title) {
        if (parentTask == null) {
            throw new IllegalArgumentException("Parent task cannot be null");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Subtask title cannot be null or empty");
        }

        Subtask subtask = new Subtask(title.trim(), parentTask);
        subtask.setId(generateUniqueId());

        // Add to repository if available
        if (taskRepository != null) {
            taskRepository.addTask(subtask);
        }

        return subtask;
    }

    /**
     * Generates a unique identifier for tasks
     * @return a unique ID string
     */
    private String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    // Getters and setters
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}