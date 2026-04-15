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
    private final ListActivitiesCommandParser listActivitiesCommandParser;
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
        this.listActivitiesCommandParser = new ListActivitiesCommandParser();
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

        while (true) {
            printMainMenu();
            System.out.println("You can use the numbered menu or type raw commands directly.");
            System.out.println("Type 'help <command-name>' to see command signature and details.");
            System.out.print("Choose an option: ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if (input.equalsIgnoreCase("0")
                    || input.equalsIgnoreCase("exit")
                    || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                scanner.close();
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                HelpPrinter.printGeneralHelp();
                continue;
            }

            if (isMenuChoice(input)) {
                boolean shouldQuit = processMenuChoiceWithRetry(input, scanner);
                if (shouldQuit) {
                    System.out.println("Goodbye!");
                    scanner.close();
                    break;
                }
            } else {
                boolean shouldQuit = processRawCommandWithRetry(input, scanner);
                if (shouldQuit) {
                    System.out.println("Goodbye!");
                    scanner.close();
                    break;
                }
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("                 MAIN MENU");
        System.out.println("==================================================");
        System.out.println(" 1. Create Task");
        System.out.println(" 2. Update Task");
        System.out.println(" 3. Create Tag");
        System.out.println(" 4. Create Project");
        System.out.println(" 5. List Projects");
        System.out.println(" 6. Add Task to Project");
        System.out.println(" 7. Create Collaborator");
        System.out.println(" 8. Assign Collaborator");
        System.out.println(" 9. List Collaborators");
        System.out.println("10. List Assignments");
        System.out.println("11. View Activity Log");
        System.out.println("12. Search Task");
        System.out.println("13. Sort Tasks");
        System.out.println("14. Import Tasks");
        System.out.println("15. Export Tasks");
        System.out.println("16. Help");
        System.out.println(" 0. Exit");
        System.out.println("==================================================");
    }

    private String mapMenuChoiceToCommand(String input, Scanner scanner) {
        switch (input) {
            case "1":
                return buildCreateTaskCommand(scanner);
            case "2":
                return buildUpdateTaskCommand(scanner);
            case "3":
                return buildCreateTagCommand(scanner);
            case "4":
                return buildCreateProjectCommand(scanner);
            case "5":
                return "list-projects";
            case "6":
                return buildAddTaskToProjectCommand(scanner);
            case "7":
                return buildCreateCollaboratorCommand(scanner);
            case "8":
                return buildAssignCollaboratorCommand(scanner);
            case "9":
                return "list-collaborators";
            case "10":
                return "list-assignments";
            case "11":
                return buildActivityLogCommand(scanner);
            case "12":
                return buildSearchTaskCommand(scanner);
            case "13":
                return buildSortTaskCommand(scanner);
            case "14":
                return buildImportCommand(scanner);
            case "15":
                return buildExportCommand(scanner);
            case "16":
                HelpPrinter.printGeneralHelp();
                return null;
            default:
                return input;
        }
    }

    private String buildCreateTaskCommand(Scanner scanner) {
        System.out.println("Choose task type:");
        System.out.println("1. Regular Task");
        System.out.println("2. Subtask");
        System.out.println("3. Recurring Task");
        System.out.print("Task type: ");
        String typeChoice = scanner.nextLine().trim();

        switch (typeChoice) {
            case "1": {
                System.out.print("Enter title: ");
                String title = scanner.nextLine().trim();

                System.out.print("Enter description (optional): ");
                String description = scanner.nextLine().trim();

                if (description.isEmpty()) {
                    return "create-task regular " + title;
                }
                return "create-task regular " + title + " | " + description;
            }

            case "2": {
                System.out.print("Enter parent task ID: ");
                String parentId = scanner.nextLine().trim();

                System.out.print("Enter subtask title: ");
                String title = scanner.nextLine().trim();

                return "create-task subtask " + parentId + " " + title;
            }

            case "3": {
                System.out.print("Enter title: ");
                String title = scanner.nextLine().trim();

                System.out.print("Enter recurrence type (DAILY, WEEKLY, MONTHLY, WEEK_DAY): ");
                String recurrenceType = scanner.nextLine().trim();

                System.out.print("Enter interval: ");
                String interval = scanner.nextLine().trim();

                System.out.print("Enter start due date (yyyy-MM-dd): ");
                String startDate = scanner.nextLine().trim();

                System.out.print("Enter number of occurrences: ");
                String occurrences = scanner.nextLine().trim();

                System.out.print("Enter description (optional): ");
                String description = scanner.nextLine().trim();

                String command = "create-task recurring " + title
                        + " | " + recurrenceType
                        + " | " + interval
                        + " | " + startDate
                        + " | " + occurrences;

                if (!description.isEmpty()) {
                    command += " | " + description;
                }

                return command;
            }

            default:
                System.out.println("Invalid task type.");
                return null;
        }
    }

    private String buildUpdateTaskCommand(Scanner scanner) {
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine().trim();

        System.out.println("Choose field to update:");
        System.out.println("1. title");
        System.out.println("2. description");
        System.out.println("3. due-date");
        System.out.println("4. priority");
        System.out.println("5. status");
        System.out.println("6. add-tag");
        System.out.println("7. remove-tag");
        System.out.println("8. complete");
        System.out.println("9. cancel");
        System.out.println("10. reopen");
        System.out.print("Field: ");
        String choice = scanner.nextLine().trim();

        String field;
        String value = "";

        switch (choice) {
            case "1":
                field = "title";
                System.out.print("Enter new title: ");
                value = scanner.nextLine().trim();
                break;
            case "2":
                field = "description";
                System.out.print("Enter new description: ");
                value = scanner.nextLine().trim();
                break;
            case "3":
                field = "due-date";
                System.out.print("Enter new due date (yyyy-MM-dd): ");
                value = scanner.nextLine().trim();
                break;
            case "4":
                field = "priority";
                System.out.print("Enter priority (LOW, MEDIUM, HIGH): ");
                value = scanner.nextLine().trim();
                break;
            case "5":
                field = "status";
                System.out.print("Enter status (OPEN, COMPLETED, CANCELLED): ");
                value = scanner.nextLine().trim();
                break;
            case "6":
                field = "add-tag";
                System.out.print("Enter tag name: ");
                value = scanner.nextLine().trim();
                break;
            case "7":
                field = "remove-tag";
                System.out.print("Enter tag name: ");
                value = scanner.nextLine().trim();
                break;
            case "8":
                field = "complete";
                break;
            case "9":
                field = "cancel";
                break;
            case "10":
                field = "reopen";
                break;
            default:
                System.out.println("Invalid update option.");
                return null;
        }

        if (field.equals("complete") || field.equals("cancel") || field.equals("reopen")) {
            return "update-task " + taskId + " " + field;
        }

        return "update-task " + taskId + " " + field + " " + value;
    }

    private String buildCreateTagCommand(Scanner scanner) {
        System.out.print("Enter tag name: ");
        String tagName = scanner.nextLine().trim();
        return "create-tag " + tagName;
    }

    private String buildCreateProjectCommand(Scanner scanner) {
        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();

        System.out.print("Enter project description (optional): ");
        String description = scanner.nextLine().trim();

        if (description.isEmpty()) {
            return "create-project " + projectName;
        }
        return "create-project " + projectName + " | " + description;
    }

    private String buildAddTaskToProjectCommand(Scanner scanner) {
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine().trim();

        System.out.print("Enter project name: ");
        String projectName = scanner.nextLine().trim();

        return "add-task-to-project " + taskId + " " + projectName;
    }

    private String buildCreateCollaboratorCommand(Scanner scanner) {
        System.out.println("Choose collaborator type:");
        System.out.println("1. Junior");
        System.out.println("2. Intermediate");
        System.out.println("3. Senior");
        System.out.print("Type: ");
        String typeChoice = scanner.nextLine().trim();

        String type;
        switch (typeChoice) {
            case "1":
                type = "junior";
                break;
            case "2":
                type = "intermediate";
                break;
            case "3":
                type = "senior";
                break;
            default:
                System.out.println("Invalid collaborator type.");
                return null;
        }

        System.out.print("Enter collaborator name: ");
        String name = scanner.nextLine().trim();

        return "create-collaborator " + type + " " + name;
    }

    private String buildAssignCollaboratorCommand(Scanner scanner) {
        System.out.print("Enter task ID: ");
        String taskId = scanner.nextLine().trim();

        System.out.print("Enter collaborator name: ");
        String collaboratorName = scanner.nextLine().trim();

        return "assign-collaborator " + taskId + " " + collaboratorName;
    }

    private String buildActivityLogCommand(Scanner scanner) {
        System.out.print("Enter task ID to filter by (or press Enter for all): ");
        String taskId = scanner.nextLine().trim();

        if (taskId.isEmpty()) {
            return "activity-log";
        }
        return "activity-log " + taskId;
    }

    private String buildSearchTaskCommand(Scanner scanner) {
        System.out.println("Choose search mode:");
        System.out.println("1. keyword");
        System.out.println("2. tag");
        System.out.println("3. status");
        System.out.println("4. priority");
        System.out.println("5. date");
        System.out.println("6. date-range");
        System.out.println("7. weekday");
        System.out.print("Mode: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter keyword: ");
                return "search-task keyword " + scanner.nextLine().trim();
            case "2":
                System.out.print("Enter tag name: ");
                return "search-task tag " + scanner.nextLine().trim();
            case "3":
                System.out.print("Enter status (OPEN, COMPLETED, CANCELLED): ");
                return "search-task status " + scanner.nextLine().trim();
            case "4":
                System.out.print("Enter priority (LOW, MEDIUM, HIGH): ");
                return "search-task priority " + scanner.nextLine().trim();
            case "5":
                System.out.print("Enter date (yyyy-MM-dd): ");
                return "search-task date " + scanner.nextLine().trim();
            case "6":
                System.out.print("Enter from date (yyyy-MM-dd): ");
                String from = scanner.nextLine().trim();
                System.out.print("Enter to date (yyyy-MM-dd): ");
                String to = scanner.nextLine().trim();
                return "search-task date-range " + from + " " + to;
            case "7":
                System.out.print("Enter weekday (1-7 or mon-sun): ");
                return "search-task weekday " + scanner.nextLine().trim();
            default:
                System.out.println("Invalid search mode.");
                return null;
        }
    }

    private String buildSortTaskCommand(Scanner scanner) {
        System.out.println("Choose sort mode:");
        System.out.println("1. due-date");
        System.out.println("2. priority");
        System.out.println("3. status");
        System.out.println("4. project");
        System.out.println("5. tag");
        System.out.print("Mode: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                return "sort-task due-date";
            case "2":
                return "sort-task priority";
            case "3":
                return "sort-task status";
            case "4":
                return "sort-task project";
            case "5":
                return "sort-task tag";
            default:
                System.out.println("Invalid sort mode.");
                return null;
        }
    }

    private String buildImportCommand(Scanner scanner) {
        System.out.print("Enter CSV file path: ");
        String path = scanner.nextLine().trim();
        return "import " + path;
    }

    private String buildExportCommand(Scanner scanner) {
        System.out.println("Choose export type:");
        System.out.println("1. Export a single task");
        System.out.println("2. Export all tasks in a project");
        System.out.println("3. Export filtered search results");
        System.out.print("Type: ");
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": {
                System.out.print("Enter task ID: ");
                String taskId = scanner.nextLine().trim();

                System.out.print("Enter output file name (.ics): ");
                String filePath = scanner.nextLine().trim();

                return "export task " + taskId + " " + filePath;
            }

            case "2": {
                System.out.print("Enter project name: ");
                String projectName = scanner.nextLine().trim();

                System.out.print("Enter output file name (.ics): ");
                String filePath = scanner.nextLine().trim();

                return "export project " + projectName + " " + filePath;
            }

            case "3": {
                System.out.print("Enter output file name (.ics): ");
                String filePath = scanner.nextLine().trim();

                return "export filtered " + filePath;
            }

            default:
                System.out.println("Invalid export option.");
                return null;
        }
    }

    private boolean isMenuChoice(String input) {
        return input.matches("^(?:[0-9]|1[0-6])$");
    }

    private boolean processMenuChoiceWithRetry(String menuChoice, Scanner scanner) {
        while (true) {
            String commandToRun = mapMenuChoiceToCommand(menuChoice, scanner);

            if (commandToRun == null) {
                return false;
            }

            boolean success = handleInput(commandToRun);

            if (success) {
                return false;
            }

            String nextAction = promptAfterError(scanner);

            switch (nextAction) {
                case "R":
                    break;
                case "M":
                    return false;
                case "Q":
                    return true;
                default:
                    return false;
            }
        }
    }

    private boolean processRawCommandWithRetry(String input, Scanner scanner) {
        String currentInput = input;

        while (true) {
            boolean success = handleInput(currentInput);

            if (success) {
                return false;
            }

            String nextAction = promptAfterError(scanner);

            switch (nextAction) {
                case "R":
                    System.out.print("Re-enter command: ");
                    currentInput = scanner.nextLine().trim();
                    if (currentInput.isEmpty()) {
                        System.out.println("Command cannot be empty.");
                        return false;
                    }
                    break;
                case "M":
                    return false;
                case "Q":
                    return true;
                default:
                    return false;
            }
        }
    }

    private String promptAfterError(Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("[R] Retry this action");
            System.out.println("[M] Return to main menu");
            System.out.println("[Q] Quit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim().toUpperCase();

            if (choice.equals("R") || choice.equals("M") || choice.equals("Q")) {
                return choice;
            }

            System.out.println("Invalid option. Please enter R, M, or Q.");
        }
    }
    private boolean handleInput(String input) {
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

                case "activity-log":
                    command = parseListActivities(args);
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
                    return true;

                default:
                    System.out.println("Unknown command. Type 'help' to see available commands.");
                    return false;
            }

            executeCommand(command);
            return true;
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
            return false;
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

    private Command parseListActivities(String args) {
        return listActivitiesCommandParser.parse(args);
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