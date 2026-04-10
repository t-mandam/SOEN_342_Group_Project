package com.taskmanagement.command;

import com.taskmanagement.domain.Tag;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.Status;
import com.taskmanagement.repository.TagCatalog;
import com.taskmanagement.repository.TagRepository;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.repository.TaskRepository;

import java.time.LocalDate;

/**
 * Command that updates a task by ID and field name.
 */
public class UpdateTaskCommand implements Command {
    private final TaskRepository taskRepository = TaskCatalog.getInstance();
    private final TagRepository tagRepository = TagCatalog.getInstance();
    private final String taskId;
    private final String field;
    private final String value;

    public UpdateTaskCommand(String taskId, String field, String value) {
        this.taskId = taskId;
        this.field = field;
        this.value = value;
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (taskId == null || taskId.trim().isEmpty()) {
            throw new IllegalStateException("Task ID cannot be null or empty");
        }
        if (field == null || field.trim().isEmpty()) {
            throw new IllegalStateException("Update field cannot be null or empty");
        }

        Task task = taskRepository.findById(taskId.trim());
        if (task == null) {
            throw new IllegalArgumentException("Task with ID '" + taskId + "' not found");
        }

        applyUpdate(task, field.trim().toLowerCase(), value != null ? value.trim() : "");
        taskRepository.updateTask(task);
        System.out.println("Task updated: " + task.getId() + " - " + task.getTitle());
    }

    private void applyUpdate(Task task, String field, String value) {
        switch (field) {
            case "title":
                requireValue(field, value);
                task.setTitle(value);
                break;

            case "description":
                task.setDescription(value);
                break;

            case "due-date":
            case "duedate":
                requireValue(field, value);
                task.setDueDate(parseDate(value));
                break;

            case "priority":
                requireValue(field, value);
                task.setPriority(Priority.valueOf(value.toUpperCase()));
                break;

            case "status":
                requireValue(field, value);
                task.setStatus(Status.valueOf(value.toUpperCase()));
                break;

            case "add-tag":
            case "tag-add":
                requireValue(field, value);
                task.addTag(resolveExistingTag(value));
                break;

            case "remove-tag":
            case "tag-remove":
                requireValue(field, value);
                task.removeTag(resolveExistingTag(value));
                break;

            case "complete":
                task.completeTask();
                break;

            case "cancel":
                task.cancelTask();
                break;

            case "reopen":
                task.reopenTask();
                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown update field '" + field + "'. Fields: title, description, due-date, priority, status, add-tag, remove-tag, complete, cancel, reopen"
                );
        }
    }

    private void requireValue(String field, String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value required for field '" + field + "'");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid due date format. Use format: yyyy-MM-dd (example: 2026-04-02)");
        }
    }

    private Tag resolveExistingTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            throw new IllegalArgumentException("Tag not found: '" + tagName + "'. Create it first using create-tag.");
        }
        return tag;
    }
}
