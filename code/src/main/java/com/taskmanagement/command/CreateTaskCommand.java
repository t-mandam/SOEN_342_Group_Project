package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.factory.TaskFactory;
import com.taskmanagement.observer.Activity;
import com.taskmanagement.observer.ActivityRecorder;
import com.taskmanagement.persistence.DatabaseConnection;
import com.taskmanagement.persistence.activity.DatabaseActivityRecorder;
import com.taskmanagement.enums.Status;
import com.taskmanagement.repository.TaskCatalog;

/**
 * Command to create a new task
 */
public class CreateTaskCommand implements Command {
    private static final int MAX_OPEN_WITHOUT_DUE_DATE = 50;
    private TaskFactory taskFactory;
    private ActivityRecorder activityRecorder;
    private String title;
    private String description;
    private Task createdTask;

    public CreateTaskCommand() {
        // Initialize with the singleton TaskCatalog
        this.taskFactory = new TaskFactory(TaskCatalog.getInstance());
        this.activityRecorder = new DatabaseActivityRecorder(DatabaseConnection.getInstance());
    }

    public CreateTaskCommand(String title) {
        this();
        this.title = title;
    }

    public CreateTaskCommand(String title, String description) {
        this(title);
        this.description = description;
    }

    @Override
    public void execute() {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty");
        }
        if (activityRecorder == null) {
            throw new IllegalStateException("Activity recorder cannot be null");
        }

        enforceOpenWithoutDueDateLimit();

        this.createdTask = taskFactory.createTask(title);
        
        if (description != null && !description.trim().isEmpty()) {
            createdTask.setDescription(description);
        }

        Activity activity = new Activity("Task " + createdTask.getId() + " created with title '" + createdTask.getTitle() + "'");
        activity.setTaskId(createdTask.getId());
        activityRecorder.record(activity);
        
        System.out.println("Task created: " + createdTask.getTitle());
        if (description != null && !description.trim().isEmpty()) {
            System.out.println("Description: " + createdTask.getDescription());
        }
        System.out.println("Task ID: " + createdTask.getId());
    }

    /**
     * Gets the created task after execution
     * @return the created Task object
     */
    public Task getCreatedTask() {
        return createdTask;
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

    public ActivityRecorder getActivityRecorder() {
        return activityRecorder;
    }

    public void setActivityRecorder(ActivityRecorder activityRecorder) {
        this.activityRecorder = activityRecorder;
    }

    private void enforceOpenWithoutDueDateLimit() {
        if (taskFactory == null || taskFactory.getTaskRepository() == null) {
            return;
        }

        int openWithoutDueDateCount = 0;
        for (Task existingTask : taskFactory.getTaskRepository().findAll()) {
            if (existingTask != null
                    && existingTask.getStatus() == Status.OPEN
                    && existingTask.getDueDate() == null) {
                openWithoutDueDateCount++;
            }
        }

        if (openWithoutDueDateCount >= MAX_OPEN_WITHOUT_DUE_DATE) {
            throw new IllegalStateException(
                    "The number of OPEN tasks without a due date cannot exceed " + MAX_OPEN_WITHOUT_DUE_DATE + "."
            );
        }
    }
}