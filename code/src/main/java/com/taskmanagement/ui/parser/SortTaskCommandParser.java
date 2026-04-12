package com.taskmanagement.ui.parser;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.SortCommand;
import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.strategy.DueDateSort;
import com.taskmanagement.strategy.PrioritySort;
import com.taskmanagement.strategy.ProjectSort;
import com.taskmanagement.strategy.SortStrategy;
import com.taskmanagement.strategy.StatusSort;
import com.taskmanagement.strategy.TagSort;

import java.util.List;

/**
 * Parses CLI arguments for sort-task command.
 */
public class SortTaskCommandParser {

    public Command parse(String args) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException(getUsage());
        }

        String mode = args.trim().toLowerCase();
        SortStrategy strategy = parseStrategy(mode);

        SortCommand sortCommand = new SortCommand(TaskCatalog.getInstance(), strategy);

        return new Command() {
            @Override
            public void execute() {
                sortCommand.execute();
                printResults(sortCommand.getSortedTasks());
            }
        };
    }

    private SortStrategy parseStrategy(String mode) {
        switch (mode) {
            case "due-date":
            case "duedate":
            case "due":
                return new DueDateSort();
            case "priority":
                return new PrioritySort();
            case "status":
                return new StatusSort();
            case "project":
                return new ProjectSort();
            case "tag":
                return new TagSort();
            default:
                throw new IllegalArgumentException(getUsage());
        }
    }

    private void printResults(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }

        System.out.println("Sorted " + tasks.size() + " task(s):");

        int idWidth = 8;
        int titleWidth = 30;
        int statusWidth = 10;
        int dueDateWidth = 12;
        int priorityWidth = 10;

        String divider = "+" + repeat("-", idWidth + 2)
                + "+" + repeat("-", titleWidth + 2)
                + "+" + repeat("-", statusWidth + 2)
                + "+" + repeat("-", dueDateWidth + 2)
                + "+" + repeat("-", priorityWidth + 2)
                + "+";

        System.out.println(divider);
        System.out.println("| " + pad("ID", idWidth)
                + " | " + pad("Title", titleWidth)
                + " | " + pad("Status", statusWidth)
                + " | " + pad("Due Date", dueDateWidth)
                + " | " + pad("Priority", priorityWidth)
                + " |");
        System.out.println(divider);

        for (Task task : tasks) {
            System.out.println("| " + pad(safe(task.getId()), idWidth)
                    + " | " + pad(safe(task.getTitle()), titleWidth)
                    + " | " + pad(safe(task.getStatus() != null ? task.getStatus().name() : null), statusWidth)
                    + " | " + pad(safe(task.getDueDate() != null ? task.getDueDate().toString() : null), dueDateWidth)
                    + " | " + pad(safe(task.getPriority() != null ? task.getPriority().name() : null), priorityWidth)
                    + " |");
        }

        System.out.println(divider);
    }

    private String getUsage() {
        return "Usage: sort-task <due-date|priority|status|project|tag>";
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String pad(String value, int width) {
        String text = value == null ? "-" : value;
        if (text.length() > width) {
            if (width <= 1) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 1) + "~";
        }
        return text + repeat(" ", width - text.length());
    }

    private String repeat(String text, int count) {
        if (count <= 0) {
            return "";
        }
        return String.valueOf(text).repeat(count);
    }
}
