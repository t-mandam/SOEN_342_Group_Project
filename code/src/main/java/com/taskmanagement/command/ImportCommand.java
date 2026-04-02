package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.util.SimpleIdGenerator;

import java.io.IOException;
import java.util.List;

/**
 * Command to import tasks from an external source
 */
public class ImportCommand implements Command {
    private TaskRepository taskRepository;
    private String importSource;
    private ImportData importData;

    public ImportCommand() {
        this.importData = new ImportData();
    }

    public ImportCommand(TaskRepository taskRepository, String importSource) {
        this.taskRepository = taskRepository;
        this.importSource = importSource;
        this.importData = new ImportData();
    }

    public ImportCommand(TaskRepository taskRepository, String importSource, ImportData importData) {
        this.taskRepository = taskRepository;
        this.importSource = importSource;
        this.importData = importData != null ? importData : new ImportData();
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (importSource == null || importSource.trim().isEmpty()) {
            throw new IllegalStateException("Import source cannot be null or empty");
        }

        try {
            List<Task> tasks = importData.readTasksFromCsv(importSource);

            for (Task task : tasks) {
                if (task.getId() == null || task.getId().trim().isEmpty()) {
                    task.setId(SimpleIdGenerator.nextId());
                }
                taskRepository.addTask(task);
            }

            System.out.println(tasks.size() + " task(s) imported from: " + importSource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to import tasks from: " + importSource, e);
        }
    }

    // Getters and setters
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public String getImportSource() {
        return importSource;
    }

    public void setImportSource(String importSource) {
        this.importSource = importSource;
    }

    public ImportData getImportData() {
        return importData;
    }

    public void setImportData(ImportData importData) {
        this.importData = importData;
    }
}
