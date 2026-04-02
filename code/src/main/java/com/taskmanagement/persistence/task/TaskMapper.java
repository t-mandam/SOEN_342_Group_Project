package com.taskmanagement.persistence.task;

import com.taskmanagement.domain.Recurrence;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.RecurrenceType;
import com.taskmanagement.enums.Status;

import java.time.LocalDate;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps Task domain objects to/from database rows
 * Converts between Task entities and ResultSet/database values
 */
public class TaskMapper {

    /**
     * Maps a database ResultSet row to a Task object
     * @param rs the ResultSet containing a task row
     * @return a Task object populated with data from the ResultSet
     * @throws SQLException if database access fails
     */
    public Task mapRowToTask(ResultSet rs) throws SQLException {
        Task task = new Task();

        task.setId(rs.getString("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));

        // Map status
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            task.setStatus(Status.valueOf(statusStr));
        }

        // Map priority
        String priorityStr = rs.getString("priority");
        if (priorityStr != null) {
            task.setPriority(Priority.valueOf(priorityStr));
        }

        // Map dates
        Timestamp creationTimestamp = rs.getTimestamp("creation_date");
        if (creationTimestamp != null) {
            task.setCreationDate(new java.util.Date(creationTimestamp.getTime()));
        }

        Date dueSqlDate = rs.getDate("due_date");
        if (dueSqlDate != null) {
            task.setDueDate(dueSqlDate.toLocalDate());
        }

        // Map recurrence if present
        String recurrenceTypeStr = rs.getString("recurrence_type");
        if (recurrenceTypeStr != null && !recurrenceTypeStr.isEmpty()) {
            Recurrence recurrence = new Recurrence();
            recurrence.setType(RecurrenceType.valueOf(recurrenceTypeStr));
            recurrence.setInterval(rs.getInt("recurrence_interval"));
            task.setRecurrence(recurrence);
        }

        // Note: project_id is not set here - manage through Project entity
        // Note: tags loaded separately through task_tags junction table

        return task;
    }

    /**
     * Maps a Task object to database column values
     * @param task the Task to map
     * @return a map of column names to values for database operations
     */
    public Map<String, Object> mapTaskToValues(Task task) {
        Map<String, Object> values = new HashMap<>();

        values.put("id", task.getId());
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("status", task.getStatus() != null ? task.getStatus().toString() : Status.OPEN.toString());
        values.put("priority", task.getPriority() != null ? task.getPriority().toString() : null);
        values.put("creation_date", task.getCreationDate() != null ? new Timestamp(task.getCreationDate().getTime()) : new Timestamp(System.currentTimeMillis()));
        values.put("due_date", toDueDateTimestamp(task.getDueDate()));

        // Map recurrence details
        if (task.getRecurrence() != null) {
            values.put("recurrence_type", task.getRecurrence().getType().toString());
            values.put("recurrence_interval", task.getRecurrence().getInterval());
        } else {
            values.put("recurrence_type", null);
            values.put("recurrence_interval", null);
        }

        return values;
    }

    /**
     * Extracts column values from a Task for an INSERT statement
     * @param task the Task to extract values from
     * @return array of values in order: [id, title, description, creation_date, due_date, priority, status, recurrence_type, recurrence_interval]
     */
    public Object[] getInsertValues(Task task) {
        return new Object[]{
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getCreationDate() != null ? new Timestamp(task.getCreationDate().getTime()) : new Timestamp(System.currentTimeMillis()),
            toDueDateTimestamp(task.getDueDate()),
            task.getPriority() != null ? task.getPriority().toString() : null,
            task.getStatus() != null ? task.getStatus().toString() : Status.OPEN.toString(),
            task.getRecurrence() != null ? task.getRecurrence().getType().toString() : null,
            task.getRecurrence() != null ? task.getRecurrence().getInterval() : null
        };
    }

    /**
     * Extracts column values from a Task for an UPDATE statement
     * @param task the Task to extract values from
     * @return array of values in order: [title, description, due_date, priority, status, recurrence_type, recurrence_interval, id]
     */
    public Object[] getUpdateValues(Task task) {
        return new Object[]{
            task.getTitle(),
            task.getDescription(),
            toDueDateTimestamp(task.getDueDate()),
            task.getPriority() != null ? task.getPriority().toString() : null,
            task.getStatus() != null ? task.getStatus().toString() : Status.OPEN.toString(),
            task.getRecurrence() != null ? task.getRecurrence().getType().toString() : null,
            task.getRecurrence() != null ? task.getRecurrence().getInterval() : null,
            task.getId()
        };
    }

    private Date toDueDateTimestamp(LocalDate dueDate) {
        return dueDate != null ? Date.valueOf(dueDate) : null;
    }
}
