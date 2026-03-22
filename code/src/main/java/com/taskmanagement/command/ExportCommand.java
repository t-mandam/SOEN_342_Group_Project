package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.search.SearchCommand;

import java.util.List;

/**
 * Command to export tasks to an external destination
 */
public class ExportCommand implements Command {
    private SearchCommand searchCommand;
    private String exportDestination;

    public ExportCommand() {}

    public ExportCommand(SearchCommand searchCommand, String exportDestination) {
        this.searchCommand = searchCommand;
        this.exportDestination = exportDestination;
    }

    @Override
    public void execute() {
        if (searchCommand == null) {
            throw new IllegalStateException("Search command cannot be null");
        }
        if (exportDestination == null || exportDestination.trim().isEmpty()) {
            throw new IllegalStateException("Export destination cannot be null or empty");
        }

        // Use the search command to get the tasks to export
        // In a real implementation, this would export tasks based on search criteria
        System.out.println("Exporting tasks to: " + exportDestination);
    }

    // Getters and setters
    public SearchCommand getSearchCommand() {
        return searchCommand;
    }

    public void setSearchCommand(SearchCommand searchCommand) {
        this.searchCommand = searchCommand;
    }

    public String getExportDestination() {
        return exportDestination;
    }

    public void setExportDestination(String exportDestination) {
        this.exportDestination = exportDestination;
    }
}