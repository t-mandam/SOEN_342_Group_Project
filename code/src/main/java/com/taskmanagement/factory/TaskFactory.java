package com.taskmanagement.factory;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Subtask;
import com.taskmanagement.domain.Recurrence;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.util.SimpleIdGenerator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
     * Creates multiple recurring task occurrences from a start date.
     * @param title the task title
     * @param recurrence the recurrence pattern
     * @param startDueDate the due date of the first occurrence
     * @param occurrences the number of tasks to generate
     * @return list of generated recurring tasks
     */
    public List<Task> createRecurringTasks(String title, Recurrence recurrence, LocalDate startDueDate, int occurrences) {
        if (startDueDate == null) {
            throw new IllegalArgumentException("Start due date cannot be null");
        }
        if (occurrences <= 0) {
            throw new IllegalArgumentException("Occurrences must be greater than 0");
        }

        List<Task> generatedTasks = new ArrayList<>();

        for (int i = 0; i < occurrences; i++) {
            Task occurrenceTask = createRecurringTask(title, recurrence);
            occurrenceTask.setDueDate(calculateOccurrenceDueDate(startDueDate, recurrence, i));

            if (taskRepository != null) {
                taskRepository.updateTask(occurrenceTask);
            }

            generatedTasks.add(occurrenceTask);
        }

        return generatedTasks;
    }

    private LocalDate calculateOccurrenceDueDate(LocalDate startDueDate, Recurrence recurrence, int occurrenceIndex) {
        if (occurrenceIndex == 0) {
            return startDueDate;
        }

        int steps = recurrence.getInterval() * occurrenceIndex;

        switch (recurrence.getType()) {
            case DAILY:
                return startDueDate.plusDays(steps);
            case WEEKLY:
                return startDueDate.plusWeeks(steps);
            case MONTHLY:
                return startDueDate.plusMonths(steps);
            case WEEK_DAY:
                return addWeekDays(startDueDate, steps);
            default:
                throw new IllegalArgumentException("Unsupported recurrence type: " + recurrence.getType());
        }
    }

    private LocalDate addWeekDays(LocalDate startDate, int weekDaysToAdd) {
        LocalDate currentDate = startDate;
        int added = 0;

        while (added < weekDaysToAdd) {
            currentDate = currentDate.plusDays(1);
            DayOfWeek day = currentDate.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                added++;
            }
        }

        return currentDate;
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
        return SimpleIdGenerator.nextId();
    }

    // Getters and setters
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}