package com.taskmanagement.ui.parser;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.ExportCommand;
import com.taskmanagement.command.ExportData;
import com.taskmanagement.command.ExportIcsCommand;
import com.taskmanagement.domain.Project;
import com.taskmanagement.domain.Task;
import com.taskmanagement.gateway.IcsCalendarExportGateway;
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.AssignmentRepository;
import com.taskmanagement.repository.ProjectCatalog;
import com.taskmanagement.repository.TaskCatalog;
import com.taskmanagement.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

public class ExportCommandParser {
    private final SearchTaskCommandParser searchTaskCommandParser;

    public ExportCommandParser(SearchTaskCommandParser searchTaskCommandParser) {
        this.searchTaskCommandParser = searchTaskCommandParser;
    }

    public Command parse(String args) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Usage:\n" +
                            "export task <task-id> <file.ics|file.csv>\n" +
                            "export project <project-name> <file.ics|file.csv>\n" +
                            "export filtered <file.ics|file.csv>"
            );
        }

        String[] parts = args.trim().split("\\s+");
        String mode = parts[0].toLowerCase();

        switch (mode) {
            case "task":
                return parseSingleTaskExport(parts);

            case "project":
                return parseProjectExport(args.trim());

            case "filtered":
                return parseFilteredExport(parts);

            default:
                throw new IllegalArgumentException(
                        "Usage:\n" +
                                "export task <task-id> <file.ics|file.csv>\n" +
                                "export project <project-name> <file.ics|file.csv>\n" +
                                "export filtered <file.ics|file.csv>"
                );
        }
    }

    private Command parseSingleTaskExport(String[] parts) {
        if (parts.length != 3) {
            throw new IllegalArgumentException("Usage: export task <task-id> <file.ics|file.csv>");
        }

        Task task = TaskCatalog.getInstance().findById(parts[1]);
        if (task == null) {
            throw new IllegalArgumentException("Task not found: " + parts[1]);
        }

        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        String filePath = parts[2];
        if (filePath.toLowerCase().endsWith(".csv")) {
            ExportCommand exportCmd = new ExportCommand(TaskCatalog.getInstance(), filePath);
            exportCmd.setUseImportCsvFormat(true);
            exportCmd.setTasksOverride(tasks);
            return exportCmd;
        } else {
            return new ExportIcsCommand(tasks, filePath, new IcsCalendarExportGateway());
        }
    }

    private Command parseProjectExport(String args) {
        String remainder = args.substring("project".length()).trim();
        int lastSpace = remainder.lastIndexOf(' ');
        if (lastSpace <= 0) {
            throw new IllegalArgumentException("Usage: export project <project-name> <file.ics|file.csv>");
        }

        String projectName = remainder.substring(0, lastSpace).trim();
        String filePath = remainder.substring(lastSpace + 1).trim();

        Project project = ProjectCatalog.getInstance().findByName(projectName);
        if (project == null) {
            throw new IllegalArgumentException("Project not found: " + projectName);
        }

        if (filePath.toLowerCase().endsWith(".csv")) {
            ExportCommand exportCmd = new ExportCommand(TaskCatalog.getInstance(), filePath);
            exportCmd.setUseImportCsvFormat(true);
            exportCmd.setTasksOverride(project.getTasks());
            return exportCmd;
        } else {
            return new ExportIcsCommand(project.getTasks(), filePath, new IcsCalendarExportGateway());
        }
    }

    private Command parseFilteredExport(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Usage: export filtered <file.ics|file.csv>");
        }

        if (searchTaskCommandParser == null || !searchTaskCommandParser.hasExecutedSearch()) {
            throw new IllegalStateException("No search results available. Run search-task first, then export filtered.");
        }

        String filePath = parts[1];
        if (filePath.toLowerCase().endsWith(".csv")) {
            ExportCommand exportCmd = new ExportCommand(TaskCatalog.getInstance(), filePath);
            exportCmd.setUseImportCsvFormat(true);
            exportCmd.setTasksOverride(searchTaskCommandParser.getLastSearchResults());
            return exportCmd;
        } else {
            return new ExportIcsCommand(
                    searchTaskCommandParser.getLastSearchResults(),
                    filePath,
                    new IcsCalendarExportGateway()
            );
        }
    }
}