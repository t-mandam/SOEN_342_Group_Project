package com.taskmanagement.command;

import com.taskmanagement.repository.TaskRepository;

/**
 * Command to import tasks from an external source
 */
public class ImportCommand implements Command {
    private TaskRepository taskRepository;
    private String importSource;

    public ImportCommand() {}

    public ImportCommand(TaskRepository taskRepository, String importSource) {
        this.taskRepository = taskRepository;
        this.importSource = importSource;
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (importSource == null || importSource.trim().isEmpty()) {
            throw new IllegalStateException("Import source cannot be null or empty");
        }

        // Placeholder implementation - in a real system this would parse the source
        // and create tasks in the repository
        System.out.println("Importing tasks from: " + importSource);
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
}