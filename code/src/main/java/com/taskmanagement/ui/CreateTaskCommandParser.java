package com.taskmanagement.ui;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.CreateRecurringTaskCommand;
import com.taskmanagement.command.CreateSubtaskCommand;
import com.taskmanagement.command.CreateTaskCommand;
import com.taskmanagement.enums.RecurrenceType;

import java.time.LocalDate;

/**
 * Parses CLI arguments for create-task commands.
 */
public class CreateTaskCommandParser {

    public Command parse(String args) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Usage: create-task <type> <arguments>\n" +
                    "Types: regular, subtask, recurring"
            );
        }

        String[] splitArgs = args.split("\\s+", 2);
        if (splitArgs.length < 2) {
            throw new IllegalArgumentException(
                    "Usage: create-task <type> <arguments>\n" +
                    "Types: regular, subtask, recurring"
            );
        }

        String createType = splitArgs[0].trim().toLowerCase();
        String createArgs = splitArgs[1].trim();

        switch (createType) {
            case "regular": {
                String[] parts = createArgs.split("\\|", 2);
                String title = parts[0].trim();
                String description = parts.length > 1 ? parts[1].trim() : null;

                CreateTaskCommand command = new CreateTaskCommand(title);
                if (description != null && !description.isEmpty()) {
                    command.setDescription(description);
                }
                return command;
            }

            case "subtask": {
                String[] parts = createArgs.split("\\s+", 2);
                if (parts.length < 2) {
                    throw new IllegalArgumentException("Usage: create-task subtask <parent-task-id> <title>");
                }

                String parentTaskId = parts[0];
                String title = parts[1];
                return new CreateSubtaskCommand(parentTaskId, title);
            }

            case "recurring": {
                String[] parts = createArgs.split("\\|");
                if (parts.length < 5) {
                    throw new IllegalArgumentException("Usage: create-task recurring <title> | <recurrence-type> | <interval> | <start-due-date> | <occurrences> [| <description>]\n" +
                            "Start due date format: yyyy-MM-dd\n" +
                            "Recurrence types: DAILY, WEEKLY, MONTHLY, WEEK_DAY");
                }

                String title = parts[0].trim();
                String recurrenceTypeStr = parts[1].trim().toUpperCase();
                String intervalStr = parts[2].trim();
                String startDueDateStr = parts[3].trim();
                String occurrencesStr = parts[4].trim();
                String description = parts.length > 5 ? parts[5].trim() : null;

                try {
                    RecurrenceType recurrenceType = RecurrenceType.valueOf(recurrenceTypeStr);
                    int interval = Integer.parseInt(intervalStr);
                    LocalDate startDueDate = LocalDate.parse(startDueDateStr);
                    int occurrences = Integer.parseInt(occurrencesStr);

                    CreateRecurringTaskCommand command = new CreateRecurringTaskCommand(
                            title,
                            recurrenceType,
                            interval,
                            startDueDate,
                            occurrences
                    );
                    if (description != null && !description.isEmpty()) {
                        command.setDescription(description);
                    }
                    return command;
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Invalid recurring arguments. Usage: create-task recurring <title> | <recurrence-type> | <interval> | <start-due-date> | <occurrences> [| <description>]\n" +
                            "Start due date format: yyyy-MM-dd\n" +
                            "Recurrence types: DAILY, WEEKLY, MONTHLY, WEEK_DAY"
                    );
                }
            }

            default:
                throw new IllegalArgumentException("Unknown create type '" + createType + "'. Types: regular, subtask, recurring");
        }
    }
}
