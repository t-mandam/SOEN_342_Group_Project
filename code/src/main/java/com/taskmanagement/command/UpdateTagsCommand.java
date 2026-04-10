package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Tag;
import com.taskmanagement.repository.TagCatalog;
import com.taskmanagement.repository.TagRepository;
import com.taskmanagement.repository.TaskRepository;

/**
 * Command to update a task's tags
 */
public class UpdateTagsCommand implements Command {
    private Task task;
    private Tag tag;
    private TaskRepository taskRepository;
    private TagRepository tagRepository;

    public UpdateTagsCommand() {
        this.tagRepository = TagCatalog.getInstance();
    }

    public UpdateTagsCommand(Task task, Tag tag, TaskRepository taskRepository) {
        this(task, tag, taskRepository, TagCatalog.getInstance());
    }

    public UpdateTagsCommand(Task task, Tag tag, TaskRepository taskRepository, TagRepository tagRepository) {
        this.task = task;
        this.tag = tag;
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
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
        if (tagRepository == null) {
            throw new IllegalStateException("Tag repository cannot be null");
        }
        if (tagRepository.findByName(tag.getName()) == null) {
            throw new IllegalArgumentException("Tag not found: '" + tag.getName() + "'. Create it first using create-tag.");
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

    public TagRepository getTagRepository() {
        return tagRepository;
    }

    public void setTagRepository(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }
}