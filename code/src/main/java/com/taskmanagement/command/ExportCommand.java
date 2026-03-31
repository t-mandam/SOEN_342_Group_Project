package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;

import java.io.IOException;
import java.util.List;

/**
 * Command to export tasks to an external destination
 */
public class ExportCommand implements Command {
    private TaskRepository taskRepository;
    private String exportDestination;
    private ExportData exportData = new ExportData();

    public ExportCommand() {}

    public ExportCommand(TaskRepository taskRepository, String exportDestination) {
        this.taskRepository = taskRepository;
        this.exportDestination = exportDestination;
    }

    public ExportCommand(TaskRepository taskRepository, String exportDestination, ExportData exportData) {
        this.taskRepository = taskRepository;
        this.exportDestination = exportDestination;
        this.exportData = exportData != null ? exportData : new ExportData();
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (exportDestination == null || exportDestination.trim().isEmpty()) {
            throw new IllegalStateException("Export destination cannot be null or empty");
        }

        try {
            List<Task> tasksToExport = taskRepository.findAll();
            exportData.writeTasksToCsv(exportDestination, tasksToExport);

            System.out.println(tasksToExport.size() + " task(s) exported to: " + exportDestination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export tasks to: " + exportDestination, e);
        }
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public String getExportDestination() {
        return exportDestination;
    }

    public void setExportDestination(String exportDestination) {
        this.exportDestination = exportDestination;
    }

    public ExportData getExportData() {
        return exportData;
    }

    public void setExportData(ExportData exportData) {
        this.exportData = exportData;
    }
}
