package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Subtask;
import com.taskmanagement.factory.TaskFactory;
import com.taskmanagement.repository.TaskCatalog;

/**
 * Command to create a subtask for an existing task
 */
public class CreateSubtaskCommand implements Command {
    private TaskFactory taskFactory;
    private TaskCatalog taskCatalog;
    private String parentTaskId;
    private String title;
    private Subtask createdSubtask;

    public CreateSubtaskCommand() {
        this.taskCatalog = TaskCatalog.getInstance();
        this.taskFactory = new TaskFactory(taskCatalog);
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

        Task parentTask = taskCatalog.findById(parentTaskId);
        if (parentTask == null) {
            throw new IllegalArgumentException("Parent task with ID '" + parentTaskId + "' not found");
        }

        this.createdSubtask = taskFactory.createSubtask(parentTask, title);
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
}
