package com.taskmanagement.gateway;

import com.taskmanagement.domain.Subtask;
import com.taskmanagement.domain.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IcsCalendarExportGateway implements CalendarExportGateway {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.BASIC_ISO_DATE;

    @Override
    public void exportTasks(List<Task> tasks, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("BEGIN:VCALENDAR");
            writer.newLine();
            writer.write("VERSION:2.0");
            writer.newLine();
            writer.write("PRODID:-//PersonalTaskManagementSystem//EN");
            writer.newLine();

            for (Task task : tasks) {
                if (task == null || task.getDueDate() == null) {
                    continue;
                }

                writeEvent(writer, task);
            }

            writer.write("END:VCALENDAR");
            writer.newLine();
        }
    }

    private void writeEvent(BufferedWriter writer, Task task) throws IOException {
        LocalDate startDate = task.getDueDate();
        LocalDate endDate = startDate.plusDays(1);

        writer.write("BEGIN:VEVENT");
        writer.newLine();
        writer.write("UID:" + UUID.randomUUID() + "@taskmanagement");
        writer.newLine();
        writer.write("DTSTAMP:20260413T000000Z");
        writer.newLine();
        writer.write("DTSTART;VALUE=DATE:" + startDate.format(DATE_FORMAT));
        writer.newLine();
        writer.write("DTEND;VALUE=DATE:" + endDate.format(DATE_FORMAT));
        writer.newLine();
        writer.write("SUMMARY:" + escape(task.getTitle()));
        writer.newLine();
        writer.write("DESCRIPTION:" + escape(buildDescription(task)));
        writer.newLine();
        writer.write("END:VEVENT");
        writer.newLine();
    }

    private String buildDescription(Task task) {
        StringBuilder sb = new StringBuilder();

        if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
            sb.append("Description: ").append(task.getDescription());
        } else {
            sb.append("Description: ");
        }

        sb.append("\\nStatus: ")
                .append(task.getStatus() != null ? task.getStatus().name() : "");

        sb.append("\\nPriority: ")
                .append(task.getPriority() != null ? task.getPriority().name() : "");

        sb.append("\\nDue Date: ")
                .append(task.getDueDate() != null ? task.getDueDate() : "");

        sb.append("\\nProject: ")
                .append(task.getProject() != null ? task.getProject().getName() : "");

        String subtaskSummary = summarizeSubtasks(task);
        if (!subtaskSummary.isEmpty()) {
            sb.append("\\nSubtasks: ").append(subtaskSummary);
        }

        return sb.toString();
    }

    private String summarizeSubtasks(Task task) {
        if (task.getSubtasks() == null || task.getSubtasks().isEmpty()) {
            return "";
        }

        return task.getSubtasks().stream()
                .filter(subtask -> subtask != null && subtask.getTitle() != null && !subtask.getTitle().trim().isEmpty())
                .map(Subtask::getTitle)
                .collect(Collectors.joining(", "));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace(",", "\\,")
                .replace(";", "\\;")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}