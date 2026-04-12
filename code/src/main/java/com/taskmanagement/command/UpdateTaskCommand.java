package com.taskmanagement.command;

import com.taskmanagement.domain.Assignment;
import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.domain.Intermediate;
import com.taskmanagement.domain.Junior;
import com.taskmanagement.domain.Senior;
import com.taskmanagement.domain.Tag;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.Status;
import com.taskmanagement.observer.Activity;
import com.taskmanagement.observer.ActivityRecorder;
import com.taskmanagement.persistence.DatabaseConnection;
import com.taskmanagement.persistence.activity.DatabaseActivityRecorder;
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.AssignmentRepository;
import com.taskmanagement.repository.TagCatalog;
import com.taskmanagement.repository.TagRepository;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.repository.TaskRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Command that updates a task by ID and field name.
 */
public class UpdateTaskCommand implements Command {
    private static final int MAX_OPEN_WITHOUT_DUE_DATE = 50;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final AssignmentRepository assignmentRepository;
    private final ActivityRecorder activityRecorder;
    private final String taskId;
    private final String field;
    private final String value;

    public UpdateTaskCommand(String taskId, String field, String value) {
        this(taskId,
                field,
                value,
                TaskCatalog.getInstance(),
                TagCatalog.getInstance(),
                AssignmentCatalog.getInstance(),
                new DatabaseActivityRecorder(DatabaseConnection.getInstance()));
    }

    public UpdateTaskCommand(String taskId,
                             String field,
                             String value,
                             TaskRepository taskRepository,
                             TagRepository tagRepository,
                             AssignmentRepository assignmentRepository,
                             ActivityRecorder activityRecorder) {
        this.taskId = taskId;
        this.field = field;
        this.value = value;
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.assignmentRepository = assignmentRepository;
        this.activityRecorder = activityRecorder;
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
        if (tagRepository == null) {
            throw new IllegalStateException("Tag repository cannot be null");
        }
        if (assignmentRepository == null) {
            throw new IllegalStateException("Assignment repository cannot be null");
        }
        if (activityRecorder == null) {
            throw new IllegalStateException("Activity recorder cannot be null");
        }

        Task task = taskRepository.findById(taskId.trim());
        if (task == null) {
            throw new IllegalArgumentException("Task with ID '" + taskId + "' not found");
        }

        String normalizedField = field.trim().toLowerCase();
        String normalizedValue = value != null ? value.trim() : "";

        String activityDescription = applyUpdate(task, normalizedField, normalizedValue);
        taskRepository.updateTask(task);
        Activity activity = new Activity(activityDescription);
        activity.setTaskId(task.getId());
        activityRecorder.record(activity);
        System.out.println("Task updated: " + task.getId() + " - " + task.getTitle());
    }

    private String applyUpdate(Task task, String field, String value) {
        switch (field) {
            case "title":
                requireValue(field, value);
                String oldTitle = task.getTitle();
                task.setTitle(value);
                return "Task " + task.getId() + " title updated from '" + safe(oldTitle) + "' to '" + safe(task.getTitle()) + "'";

            case "description":
                String oldDescription = task.getDescription();
                task.setDescription(value);
                return "Task " + task.getId() + " description updated from '" + safe(oldDescription) + "' to '" + safe(task.getDescription()) + "'";

            case "due-date":
            case "duedate":
                requireValue(field, value);
                LocalDate oldDueDate = task.getDueDate();
                task.setDueDate(parseDate(value));
                return "Task " + task.getId() + " due date updated from '" + safe(oldDueDate) + "' to '" + safe(task.getDueDate()) + "'";

            case "priority":
                requireValue(field, value);
                Priority oldPriority = task.getPriority();
                task.setPriority(Priority.valueOf(value.toUpperCase()));
                return "Task " + task.getId() + " priority updated from '" + safe(oldPriority) + "' to '" + safe(task.getPriority()) + "'";

            case "status":
                requireValue(field, value);
                Status targetStatus = Status.valueOf(value.toUpperCase());
                ensureOpenWithoutDueDateLimitForStatusChange(task, targetStatus);
                ensureCollaboratorCapacityForOpening(task, targetStatus);
                Status oldStatus = task.getStatus();
                task.setStatus(targetStatus);
                return "Task " + task.getId() + " status updated from '" + safe(oldStatus) + "' to '" + safe(task.getStatus()) + "'";

            case "add-tag":
            case "tag-add":
                requireValue(field, value);
                Tag tagToAdd = resolveExistingTag(value);
                task.addTag(tagToAdd);
                return "Tag '" + safe(tagToAdd.getName()) + "' added to task " + task.getId();

            case "remove-tag":
            case "tag-remove":
                requireValue(field, value);
                Tag tagToRemove = resolveExistingTag(value);
                task.removeTag(tagToRemove);
                return "Tag '" + safe(tagToRemove.getName()) + "' removed from task " + task.getId();

            case "complete":
                boolean completed = task.completeTask();
                if (completed) {
                    return "Task " + task.getId() + " marked as COMPLETED";
                }
                return "Task " + task.getId() + " complete requested but task was already COMPLETED";

            case "cancel":
                boolean cancelled = task.cancelTask();
                if (cancelled) {
                    return "Task " + task.getId() + " marked as CANCELLED";
                }
                return "Task " + task.getId() + " cancel requested but task was already CANCELLED";

            case "reopen":
                ensureOpenWithoutDueDateLimitForStatusChange(task, Status.OPEN);
                ensureCollaboratorCapacityForOpening(task, Status.OPEN);
                boolean reopened = task.reopenTask();
                if (reopened) {
                    return "Task " + task.getId() + " reopened to OPEN";
                }
                return "Task " + task.getId() + " reopen requested but task was already OPEN";

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

    private String safe(Object value) {
        if (value == null) {
            return "-";
        }

        String text = String.valueOf(value).trim();
        return text.isEmpty() ? "-" : text;
    }

    private void ensureOpenWithoutDueDateLimitForStatusChange(Task task, Status newStatus) {
        if (task == null) {
            return;
        }

        boolean oldQualifies = qualifiesForOpenWithoutDueDate(task.getStatus(), task.getDueDate());
        boolean newQualifies = qualifiesForOpenWithoutDueDate(newStatus, task.getDueDate());

        if (!newQualifies || oldQualifies == newQualifies) {
            return;
        }

        int openWithoutDueDateCount = 0;
        for (Task existingTask : taskRepository.findAll()) {
            if (existingTask != null
                    && qualifiesForOpenWithoutDueDate(existingTask.getStatus(), existingTask.getDueDate())) {
                openWithoutDueDateCount++;
            }
        }

        if (openWithoutDueDateCount >= MAX_OPEN_WITHOUT_DUE_DATE) {
            throw new IllegalStateException(
                    "The number of OPEN tasks without a due date cannot exceed " + MAX_OPEN_WITHOUT_DUE_DATE + "."
            );
        }
    }

    private boolean qualifiesForOpenWithoutDueDate(Status status, LocalDate dueDate) {
        return status == Status.OPEN && dueDate == null;
    }

    private void ensureCollaboratorCapacityForOpening(Task task, Status newStatus) {
        if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
            return;
        }
        if (newStatus != Status.OPEN || task.getStatus() == Status.OPEN) {
            return;
        }

        List<Assignment> taskAssignments = assignmentRepository.findByTaskId(task.getId().trim());
        for (Assignment assignment : taskAssignments) {
            if (assignment == null || assignment.getCollaborator() == null) {
                continue;
            }

            Collaborator collaborator = assignment.getCollaborator();
            String collaboratorName = collaborator.getName();
            if (collaboratorName == null || collaboratorName.trim().isEmpty()) {
                continue;
            }

            int currentOpenTasks = countOpenAssignmentsForCollaborator(collaboratorName.trim());
            int maxOpenTasks = getMaxOpenTasks(collaborator);
            if (currentOpenTasks >= maxOpenTasks) {
                throw new IllegalStateException(
                        "Collaborator '" + collaboratorName + "' cannot exceed open task limit (" + maxOpenTasks + ")."
                );
            }
        }
    }

    private int countOpenAssignmentsForCollaborator(String collaboratorName) {
        int count = 0;
        List<Assignment> assignments = assignmentRepository.findByCollaboratorName(collaboratorName);
        for (Assignment assignment : assignments) {
            if (assignment != null
                    && assignment.getTask() != null
                    && assignment.getTask().getStatus() == Status.OPEN) {
                count++;
            }
        }
        return count;
    }

    private int getMaxOpenTasks(Collaborator collaborator) {
        if (collaborator instanceof Junior) {
            return ((Junior) collaborator).getMaxOpenTasks();
        }
        if (collaborator instanceof Intermediate) {
            return ((Intermediate) collaborator).getMaxOpenTasks();
        }
        if (collaborator instanceof Senior) {
            return ((Senior) collaborator).getMaxOpenTasks();
        }

        return Integer.MAX_VALUE;
    }
}
