package com.taskmanagement.command;

import com.taskmanagement.domain.Tag;
import com.taskmanagement.domain.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles writing task data to a CSV destination.
 */
public class ExportData {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DUE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void writeTasksToCsv(String exportDestination, List<Task> tasks) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportDestination))) {
            writer.write("id,title,description,creationDate,dueDate,priority,status,tags");
            writer.newLine();

            for (Task task : tasks) {
                writer.write(toCsvRow(task));
                writer.newLine();
            }
        }
    }

    private String toCsvRow(Task task) {
        return String.join(",",
                escape(task.getId()),
                escape(task.getTitle()),
                escape(task.getDescription()),
                escape(formatDateTime(task.getCreationDate())),
                escape(formatDueDate(task.getDueDate())),
                escape(task.getPriority() != null ? task.getPriority().name() : ""),
                escape(task.getStatus() != null ? task.getStatus().name() : ""),
                escape(formatTags(task))
        );
    }

    private String formatDateTime(java.util.Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(DATE_TIME_FORMAT).format(date);
    }

    private String formatDueDate(LocalDate dueDate) {
        if (dueDate == null) {
            return "";
        }
        return dueDate.format(DUE_DATE_FORMATTER);
    }

    private String formatTags(Task task) {
        if (task.getTags() == null || task.getTags().isEmpty()) {
            return "";
        }

        return task.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.joining("|"));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }
}
