package com.taskmanagement.ui;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.UpdateTaskCommand;

/**
 * Parses CLI arguments for update-task command.
 */
public class UpdateTaskCommandParser {

    public Command parse(String args) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Usage: update-task <task-id> <field> <value>\n" +
                    "Fields: title, description, due-date, priority, status, add-tag, remove-tag, complete, cancel, reopen\n" +
                    "Example: update-task 1 title New task title"
            );
        }

        String[] parts = args.split("\\s+", 3);
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: update-task <task-id> <field> <value>\n" +
                    "Fields: title, description, due-date, priority, status, add-tag, remove-tag, complete, cancel, reopen"
            );
        }

        String taskId = parts[0].trim();
        String field = parts[1].trim().toLowerCase();
        String value = parts.length > 2 ? parts[2].trim() : "";
        return new UpdateTaskCommand(taskId, field, value);
    }
}
