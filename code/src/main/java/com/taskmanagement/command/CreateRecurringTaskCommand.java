package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Recurrence;
import com.taskmanagement.enums.RecurrenceType;
import com.taskmanagement.factory.TaskFactory;
import com.taskmanagement.repository.TaskCatalog;

import java.time.LocalDate;
import java.util.List;

/**
 * Command to create a recurring task
 */
public class CreateRecurringTaskCommand implements Command {
    private TaskFactory taskFactory;
    private String title;
    private String description;
    private RecurrenceType recurrenceType;
    private int interval;
    private LocalDate startDueDate;
    private int occurrences;
    private Task createdTask;
    private List<Task> createdTasks;

    public CreateRecurringTaskCommand() {
        // Initialize with the singleton TaskCatalog
        this.taskFactory = new TaskFactory(TaskCatalog.getInstance());
        this.occurrences = 1;
    }

    public CreateRecurringTaskCommand(String title, RecurrenceType recurrenceType, int interval, LocalDate startDueDate, int occurrences) {
        this();
        this.title = title;
        this.recurrenceType = recurrenceType;
        this.interval = interval;
        this.startDueDate = startDueDate;
        this.occurrences = occurrences;
    }

    public CreateRecurringTaskCommand(String title, String description, RecurrenceType recurrenceType, int interval, LocalDate startDueDate, int occurrences) {
        this(title, recurrenceType, interval, startDueDate, occurrences);
        this.description = description;
    }

    @Override
    public void execute() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }

        if (recurrenceType == null) {
            throw new IllegalArgumentException("Recurrence type cannot be null");
        }

        if (interval <= 0) {
            throw new IllegalArgumentException("Interval must be greater than 0");
        }
        if (startDueDate == null) {
            throw new IllegalArgumentException("Start due date cannot be null");
        }
        if (occurrences <= 0) {
            throw new IllegalArgumentException("Occurrences must be greater than 0");
        }

        Recurrence recurrence = new Recurrence(recurrenceType, interval);
        this.createdTasks = taskFactory.createRecurringTasks(title, recurrence, startDueDate, occurrences);
        this.createdTask = createdTasks.get(0);

        if (description != null && !description.trim().isEmpty()) {
            for (Task task : createdTasks) {
                task.setDescription(description);
            }
        }

        System.out.println("Recurring tasks created: " + createdTasks.size());
        System.out.println("Title: " + createdTask.getTitle());
        System.out.println("Recurrence: Every " + interval + " " + recurrenceType.toString().toLowerCase());
        System.out.println("Start due date: " + startDueDate);
        if (description != null && !description.trim().isEmpty()) {
            System.out.println("Description: " + description);
        }
        for (Task task : createdTasks) {
            System.out.println("Task ID: " + task.getId() + " | Due date: " + task.getDueDate());
        }
    }

    /**
     * Gets the created task after execution
     * @return the created Task object
     */
    public Task getCreatedTask() {
        return createdTask;
    }

    public List<Task> getCreatedTasks() {
        return createdTasks;
    }

    /**
     * Sets the title for the task to be created
     * @param title the task title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the title
     * @return the task title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the description for the task to be created
     * @param description the task description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the description
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the recurrence type
     * @param recurrenceType the recurrence type
     */
    public void setRecurrenceType(RecurrenceType recurrenceType) {
        this.recurrenceType = recurrenceType;
    }

    /**
     * Gets the recurrence type
     * @return the recurrence type
     */
    public RecurrenceType getRecurrenceType() {
        return recurrenceType;
    }

    /**
     * Sets the interval for recurrence
     * @param interval the interval
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * Gets the interval for recurrence
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    public LocalDate getStartDueDate() {
        return startDueDate;
    }

    public void setStartDueDate(LocalDate startDueDate) {
        this.startDueDate = startDueDate;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    /**
     * Sets the task factory
     * @param taskFactory the factory to use
     */
    public void setTaskFactory(TaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    /**
     * Gets the task factory
     * @return the task factory
     */
    public TaskFactory getTaskFactory() {
        return taskFactory;
    }
}
