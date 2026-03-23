package com.taskmanagement.command;

import com.taskmanagement.domain.Tag;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.Status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Handles reading task data from a CSV source.
 */
public class ImportData {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public List<Task> readTasksFromCsv(String importSource) throws IOException {
        List<Task> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(importSource))) {
            String line = reader.readLine();

            // Skip header if present
            if (line == null) {
                return tasks;
            }

            if (!looksLikeHeader(line)) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        }

        return tasks;
    }

    private boolean looksLikeHeader(String line) {
        String normalized = line.toLowerCase();
        return normalized.contains("title") || normalized.contains("description") || normalized.contains("priority");
    }

    private Task parseTask(String line) {
        List<String> columns = parseCsvLine(line);

        // Expected:
        // 0=id, 1=title, 2=description, 3=creationDate, 4=dueDate, 5=priority, 6=status, 7=tags
        if (columns.size() < 8) {
            throw new IllegalArgumentException("Invalid CSV row. Expected 8 columns but got " + columns.size());
        }

        Task task = new Task();
        task.setId(emptyToNull(columns.get(0)));
        task.setTitle(emptyToNull(columns.get(1)));
        task.setDescription(emptyToNull(columns.get(2)));
        task.setCreationDate(parseDate(columns.get(3)));
        task.setDueDate(parseDate(columns.get(4)));
        task.setPriority(parsePriority(columns.get(5)));
        task.setStatus(parseStatus(columns.get(6)));
        task.setTags(parseTags(columns.get(7)));

        return task;
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        values.add(current.toString().trim());
        return values;
    }

    private Date parseDate(String value) {
        String cleaned = emptyToNull(value);
        if (cleaned == null) {
            return null;
        }

        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(cleaned);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + cleaned + ". Expected " + DATE_FORMAT, e);
        }
    }

    private Priority parsePriority(String value) {
        String cleaned = emptyToNull(value);
        if (cleaned == null) {
            return null;
        }
        return Priority.valueOf(cleaned.trim().toUpperCase());
    }

    private Status parseStatus(String value) {
        String cleaned = emptyToNull(value);
        if (cleaned == null) {
            return Status.OPEN;
        }
        return Status.valueOf(cleaned.trim().toUpperCase());
    }

    private List<Tag> parseTags(String value) {
        List<Tag> tags = new ArrayList<>();
        String cleaned = emptyToNull(value);

        if (cleaned == null) {
            return tags;
        }

        String[] split = cleaned.split("\\|");
        for (String tagName : split) {
            String trimmed = tagName.trim();
            if (!trimmed.isEmpty()) {
                tags.add(new Tag(trimmed));
            }
        }

        return tags;
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
