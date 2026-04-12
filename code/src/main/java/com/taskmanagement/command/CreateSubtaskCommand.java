package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Subtask;
import com.taskmanagement.factory.TaskFactory;
import com.taskmanagement.observer.Activity;
import com.taskmanagement.observer.ActivityRecorder;
import com.taskmanagement.persistence.DatabaseConnection;
import com.taskmanagement.persistence.activity.DatabaseActivityRecorder;
import com.taskmanagement.enums.Status;
import com.taskmanagement.repository.TaskCatalog;

/**
 * Command to create a subtask for an existing task
 */
public class CreateSubtaskCommand implements Command {
    private static final int MAX_OPEN_WITHOUT_DUE_DATE = 50;
    private TaskFactory taskFactory;
    private TaskCatalog taskCatalog;
    private ActivityRecorder activityRecorder;
    private String parentTaskId;
    private String title;
    private Subtask createdSubtask;

    public CreateSubtaskCommand() {
        this.taskCatalog = TaskCatalog.getInstance();
        this.taskFactory = new TaskFactory(taskCatalog);
        this.activityRecorder = new DatabaseActivityRecorder(DatabaseConnection.getInstance());
    }

    public CreateSubtaskCommand(String parentTaskId, String title) {
        this();
        this.parentTaskId = parentTaskId;
        this.title = title;
    }

    @Override
    public void execute() {
        if (parentTaskId == null || parentTaskId.trim().isEmpty()) {
            throw new IllegalArgumentException("Parent task ID cannot be null or empty");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Subtask title cannot be null or empty");
        }
        if (activityRecorder == null) {
            throw new IllegalStateException("Activity recorder cannot be null");
        }

        enforceOpenWithoutDueDateLimit();

        Task parentTask = taskCatalog.findById(parentTaskId);
        if (parentTask == null) {
            throw new IllegalArgumentException("Parent task with ID '" + parentTaskId + "' not found");
        }

        this.createdSubtask = taskFactory.createSubtask(parentTask, title);

        Activity activity = new Activity("Subtask " + createdSubtask.getId() + " created under task " + parentTask.getId());
        activity.setTaskId(createdSubtask.getId());
        activityRecorder.record(activity);

        System.out.println("Subtask created: " + createdSubtask.getTitle());
        System.out.println("Parent Task: " + parentTask.getTitle());
        System.out.println("Subtask ID: " + createdSubtask.getId());
    }

    /**
     * Gets the created subtask after execution
     * @return the created Subtask object
     */
    public Subtask getCreatedSubtask() {
        return createdSubtask;
    }

    /**
     * Sets the parent task ID
     * @param parentTaskId the parent task ID
     */
    public void setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    /**
     * Gets the parent task ID
     * @return the parent task ID
     */
    public String getParentTaskId() {
        return parentTaskId;
    }

    /**
     * Sets the title for the subtask to be created
     * @param title the subtask title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the title
     * @return the subtask title
     */
    public String getTitle() {
        return title;
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
        int openWithoutDueDateCount = 0;
        for (Task existingTask : taskCatalog.findAll()) {
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
