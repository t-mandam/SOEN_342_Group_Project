package com.taskmanagement.command;

import com.taskmanagement.domain.Project;
import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;
import com.taskmanagement.enums.Status;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles reading task data from a CSV source.
 */
public class ImportData {
    private static final String DUE_DATE_FORMAT = "yyyy-MM-dd";

    public List<ImportedTaskData> readTasksFromCsv(String importSource) throws IOException {
        List<ImportedTaskData> tasks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(importSource))) {
            String line = reader.readLine();

            // Skip header if present
            if (line == null) {
                return tasks;
            }

            if (!looksLikeHeader(line)) {
                ImportedTaskData taskData = parseTask(line);
                if (taskData != null) {
                    tasks.add(taskData);
                }
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                ImportedTaskData taskData = parseTask(line);
                if (taskData != null) {
                    tasks.add(taskData);
                }
            }
        }

        return tasks;
    }

    private boolean looksLikeHeader(String line) {
        String normalized = line.toLowerCase();
        return normalized.contains("taskname") || normalized.contains("description") || normalized.contains("projectname");
    }

    private ImportedTaskData parseTask(String line) {
        List<String> columns = parseCsvLine(line);

        // Expected:
        // 0=TaskName, 1=Description, 2=Subtask, 3=Status, 4=Priority, 5=DueDate,
        // 6=ProjectName, 7=ProjectDescription, 8=Collaborator, 9=CollaboratorCategory
        if (columns.size() < 10) {
            throw new IllegalArgumentException("Invalid CSV row. Expected 10 columns but got " + columns.size());
        }

        Task task = new Task();
        task.setTitle(emptyToNull(columns.get(0)));
        task.setDescription(emptyToNull(columns.get(1)));
        task.setStatus(parseStatus(columns.get(3)));
        task.setPriority(parsePriority(columns.get(4)));
        task.setDueDate(parseDueDate(columns.get(5)));

        String projectName = emptyToNull(columns.get(6));
        if (projectName != null) {
            task.setProject(new Project(projectName, emptyToNull(columns.get(7))));
        }

        String subtaskTitle = emptyToNull(columns.get(2));
        String collaboratorName = emptyToNull(columns.get(8));
        String collaboratorCategory = emptyToNull(columns.get(9));

        return new ImportedTaskData(task, subtaskTitle, collaboratorName, collaboratorCategory);
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

    private java.time.LocalDate parseDueDate(String value) {
        String cleaned = emptyToNull(value);
        if (cleaned == null) {
            return null;
        }

        try {
            return LocalDate.parse(cleaned);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid due date format: " + cleaned + ". Expected " + DUE_DATE_FORMAT, e);
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

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static class ImportedTaskData {
        private final Task task;
        private final String subtaskTitle;
        private final String collaboratorName;
        private final String collaboratorCategory;

        public ImportedTaskData(Task task, String subtaskTitle, String collaboratorName, String collaboratorCategory) {
            this.task = task;
            this.subtaskTitle = subtaskTitle;
            this.collaboratorName = collaboratorName;
            this.collaboratorCategory = collaboratorCategory;
        }

        public Task getTask() {
            return task;
        }

        public String getSubtaskTitle() {
            return subtaskTitle;
        }

        public String getCollaboratorName() {
            return collaboratorName;
        }

        public String getCollaboratorCategory() {
            return collaboratorCategory;
        }
    }
}
