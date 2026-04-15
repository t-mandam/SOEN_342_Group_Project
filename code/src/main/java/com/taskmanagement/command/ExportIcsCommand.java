package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.gateway.CalendarExportGateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportIcsCommand implements Command {
    private final List<Task> tasks;
    private final String filePath;
    private final CalendarExportGateway gateway;

    public ExportIcsCommand(List<Task> tasks, String filePath, CalendarExportGateway gateway) {
        this.tasks = tasks == null ? new ArrayList<>() : new ArrayList<>(tasks);
        this.filePath = filePath;
        this.gateway = gateway;
    }

    @Override
    public void execute() {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Export file path cannot be empty.");
        }

        if (!filePath.toLowerCase().endsWith(".ics")) {
            throw new IllegalArgumentException("Export file must use the .ics extension.");
        }

        try {
            gateway.exportTasks(tasks, filePath);
            long eligibleCount = tasks.stream().filter(t -> t != null && t.getDueDate() != null).count();
            System.out.println(eligibleCount + " task(s) exported to: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export iCal file: " + filePath, e);
        }
    }
}
