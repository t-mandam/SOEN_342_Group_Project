package com.taskmanagement.ui;

import com.taskmanagement.command.*;
import com.taskmanagement.persistence.AppPersistenceManager;
import com.taskmanagement.ui.parser.*;
import java.util.Scanner;

public class Console {
    private final AppPersistenceManager persistenceManager;
    private final CreateTaskCommandParser createTaskCommandParser;
    private final CreateTagCommandParser createTagCommandParser;
    private final CreateProjectCommandParser createProjectCommandParser;
    private final ListProjectsCommandParser listProjectsCommandParser;
    private final AddTaskToProjectCommandParser addTaskToProjectCommandParser;
    private final CreateCollaboratorCommandParser createCollaboratorCommandParser;
    private final AssignCollaboratorCommandParser assignCollaboratorCommandParser;
    private final ListCollaboratorsCommandParser listCollaboratorsCommandParser;
    private final ListAssignmentsCommandParser listAssignmentsCommandParser;
    private final ImportCommandParser importCommandParser;
    private final ExportCommandParser exportCommandParser;
    private final SortTaskCommandParser sortTaskCommandParser;
    private final UpdateTaskCommandParser updateTaskCommandParser;
    private final SearchTaskCommandParser searchTaskCommandParser;

    public Console() {
        this(new AppPersistenceManager());
    }

    public Console(AppPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
        this.createTaskCommandParser = new CreateTaskCommandParser();
        this.createTagCommandParser = new CreateTagCommandParser();
        this.createProjectCommandParser = new CreateProjectCommandParser();
        this.listProjectsCommandParser = new ListProjectsCommandParser();
        this.addTaskToProjectCommandParser = new AddTaskToProjectCommandParser();
        this.createCollaboratorCommandParser = new CreateCollaboratorCommandParser();
        this.assignCollaboratorCommandParser = new AssignCollaboratorCommandParser();
        this.listCollaboratorsCommandParser = new ListCollaboratorsCommandParser();
        this.listAssignmentsCommandParser = new ListAssignmentsCommandParser();
        this.importCommandParser = new ImportCommandParser();
        this.searchTaskCommandParser = new SearchTaskCommandParser();
        this.exportCommandParser = new ExportCommandParser(this.searchTaskCommandParser);
        this.sortTaskCommandParser = new SortTaskCommandParser();
        this.updateTaskCommandParser = new UpdateTaskCommandParser();
    }

    public void executeCommand(Command command) {
        if (command != null) {
            command.execute();
            if (isStateChangingCommand(command)) {
                persistenceManager.saveToDatabase();
            }
        }
    }

    private boolean isStateChangingCommand(Command command) {
        String className = command.getClass().getSimpleName();
        return className.startsWith("Create")
                || className.startsWith("Update")
                || className.startsWith("Assign")
            || className.startsWith("Add")
            || className.startsWith("Import");
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        HelpPrinter.printGeneralHelp();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                scanner.close();
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                HelpPrinter.printGeneralHelp();
                continue;
            }

            handleInput(input);
        }
    }

    private void handleInput(String input) {
        String[] parts = input.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1].trim() : "";

        try {
            Command command = null;
            switch (commandName) {

                case "create-task":
                    command = parseCreateTask(args);
                    break;

                case "update-task":
                    command = parseUpdateTask(args);
                    break;

                case "create-tag":
                    command = parseCreateTag(args);
                    break;

                case "create-project":
                    command = parseCreateProject(args);
                    break;

                case "list-projects":
                    command = parseListProjects(args);
                    break;

                case "add-task-to-project":
                    command = parseAddTaskToProject(args);
                    break;

                case "create-collaborator":
                    command = parseCreateCollaborator(args);
                    break;

                case "assign-collaborator":
                    command = parseAssignCollaborator(args);
                    break;

                case "list-collaborators":
                    command = parseListCollaborators(args);
                    break;

                case "list-assignments":
                    command = parseListAssignments(args);
                    break;

                case "search-task":
                    command = parseSearchTask(args);
                    break;

                case "import":
                    command = parseImport(args);
                    break;

                case "export":
                    command = parseExport(args);
                    break;

                case "sort-task":
                    command = parseSortTask(args);
                    break;

                case "help":
                    HelpPrinter.printCommandHelp(args);
                    return;

                default:
                    System.out.println("Unknown command. Type 'help' to see available commands.");
                    return;
            }

            executeCommand(command);
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * Parses arguments for update-task command
     * Usage: update-task <task-id> <field> <value>
     */
    private Command parseUpdateTask(String args) {
        return updateTaskCommandParser.parse(args);
    }

    /**
     * Parses arguments for create-task command
     * Usage: create-task <type> <arguments>
     */
    private Command parseCreateTask(String args) {
        return createTaskCommandParser.parse(args);
    }

    private Command parseSearchTask(String args) {
        return searchTaskCommandParser.parse(args);
    }

    private Command parseCreateTag(String args) {
        return createTagCommandParser.parse(args);
    }

    private Command parseCreateProject(String args) {
        return createProjectCommandParser.parse(args);
    }

    private Command parseListProjects(String args) {
        return listProjectsCommandParser.parse(args);
    }

    private Command parseAddTaskToProject(String args) {
        return addTaskToProjectCommandParser.parse(args);
    }

    private Command parseCreateCollaborator(String args) {
        return createCollaboratorCommandParser.parse(args);
    }

    private Command parseAssignCollaborator(String args) {
        return assignCollaboratorCommandParser.parse(args);
    }

    private Command parseListCollaborators(String args) {
        return listCollaboratorsCommandParser.parse(args);
    }

    private Command parseListAssignments(String args) {
        return listAssignmentsCommandParser.parse(args);
    }

    private Command parseImport(String args) {
        return importCommandParser.parse(args);
    }

    private Command parseExport(String args) {
        return exportCommandParser.parse(args);
    }

    private Command parseSortTask(String args) {
        return sortTaskCommandParser.parse(args);
    }

    public void initialize() {
        persistenceManager.loadFromDatabase();
    }
}