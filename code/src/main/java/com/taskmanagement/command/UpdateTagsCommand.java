package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Tag;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to update a task's tags
 */
public class UpdateTagsCommand implements Command {
    private Task task;
    private Tag tag;
    private TaskRepository taskRepository;

    public UpdateTagsCommand() {}

    public UpdateTagsCommand(Task task, Tag tag, TaskRepository taskRepository) {
        this.task = task;
        this.tag = tag;
        this.taskRepository = taskRepository;
    }

    @Override
    public void execute() {
        if (task == null) {
            throw new IllegalStateException("Task cannot be null");
        }
        if (tag == null) {
            throw new IllegalStateException("Tag cannot be null");
        }
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }

        // Default behavior is to add the tag
        addTag();
    }

    /**
     * Adds the tag to the task
     */
    public void addTag() {
        if (task != null && tag != null) {
            task.addTag(tag);
            if (taskRepository != null) {
                taskRepository.updateTask(task);
            }
        }
    }

    /**
     * Removes the tag from the task
     */
    public void removeTag() {
        if (task != null && tag != null) {
            task.removeTag(tag);
            if (taskRepository != null) {
                taskRepository.updateTask(task);
            }
        }
    }

    // Getters and setters
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}