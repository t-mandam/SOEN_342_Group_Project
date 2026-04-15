package com.taskmanagement.ui;

/**
 * Centralized help output for CLI commands.
 */
public final class HelpPrinter {

    private HelpPrinter() {
    }

    public static void printGeneralHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("create-task");
        System.out.println("create-tag");
        System.out.println("create-project");
        System.out.println("list-projects");
        System.out.println("add-task-to-project");
        System.out.println("create-collaborator");
        System.out.println("assign-collaborator");
        System.out.println("list-collaborators");
        System.out.println("list-assignments");
        System.out.println("activity-log");
        System.out.println("import");
        System.out.println("export");
        System.out.println("sort-task");
        System.out.println("update-task");
        System.out.println("search-task");
        System.out.println("help");
        System.out.println();
        System.out.println("Type 'help <command-name>' to see command signature and details.");
        System.out.println("You can use the numbered menu or type raw commands directly.");
        System.out.println();
    }

    public static void printCommandHelp(String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            printGeneralHelp();
            return;
        }

        switch (commandName.trim().toLowerCase()) {
            case "create-task":
                System.out.println("create-task <type> <arguments>");
                System.out.println("Types:");
                System.out.println("  regular  -> create-task regular <title> [| <description>]");
                System.out.println("  subtask  -> create-task subtask <parent-task-id> <title>");
                System.out.println("  recurring -> create-task recurring <title> | <recurrence-type> | <interval> | <start-due-date> | <occurrences> [| <description>]");
                System.out.println("  start-due-date format: yyyy-MM-dd");
                System.out.println("Recurrence types: DAILY, WEEKLY, MONTHLY, WEEK_DAY");
                break;

            case "update-task":
                System.out.println("update-task <task-id> <field> <value>");
                System.out.println("Fields: title, description, due-date, priority, status, add-tag, remove-tag, complete, cancel, reopen");
                System.out.println("due-date format: yyyy-MM-dd (example: 2026-04-02)");
                System.out.println("For add-tag/remove-tag, value must be an existing tag from create-tag.");
                break;

            case "create-tag":
                System.out.println("create-tag <tag-name>");
                System.out.println("Creates a reusable tag for task updates.");
                break;

            case "create-project":
                System.out.println("create-project <project-name> [| <description>]");
                System.out.println("Creates a project with optional description.");
                break;

            case "list-projects":
                System.out.println("list-projects");
                System.out.println("Lists all saved projects.");
                break;

            case "add-task-to-project":
                System.out.println("add-task-to-project <task-id> <project-name>");
                System.out.println("Adds an existing task to an existing project.");
                break;

            case "create-collaborator":
                System.out.println("create-collaborator <type> <name>");
                System.out.println("Types: junior, intermediate, senior");
                break;

            case "assign-collaborator":
                System.out.println("assign-collaborator <task-id> <collaborator-name>");
                System.out.println("Assigns an existing collaborator to an existing task.");
                System.out.println("Task must be in OPEN status to be assigned.");
                System.out.println("Task must already be part of a project.");
                System.out.println("Automatically creates a linked subtask named: '<collaborator-name> - <task-title>'");
                System.out.println("Open-task limits apply based on collaborator level.");
                break;

            case "list-collaborators":
                System.out.println("list-collaborators");
                System.out.println("Lists all saved collaborators.");
                break;

            case "list-assignments":
                System.out.println("list-assignments");
                System.out.println("Lists all task-collaborator assignments.");
                break;

            case "activity-log":
                System.out.println("activity-log [task-id]");
                System.out.println("Lists activity log entries.");
                System.out.println("Optional task-id filters entries for one task only.");
                break;

            case "search-task":
                System.out.println("search-task <mode> <args> [| <mode> <args> ...]");
                System.out.println("No args: lists OPEN tasks sorted by due date (ascending)");
                System.out.println("Modes:");
                System.out.println("  keyword <text>");
                System.out.println("  tag <tag-name>");
                System.out.println("  status <OPEN|COMPLETED|CANCELLED>");
                System.out.println("  priority <LOW|MEDIUM|HIGH>");
                System.out.println("  date <yyyy-MM-dd>");
                System.out.println("  date-range <from-yyyy-MM-dd> <to-yyyy-MM-dd>");
                System.out.println("  weekday <1-7|sun|mon|...|sat>");
                System.out.println("Example: search-task keyword report | status OPEN | date-range 2026-04-01 2026-04-30");
                break;

            case "import":
                System.out.println("import <csv-file-path>");
                System.out.println("Expected CSV columns:");
                System.out.println("  TaskName, Description, Subtask, Status, Priority, DueDate, ProjectName, ProjectDescription, Collaborator, CollaboratorCategory");
                break;

            case "export":
                System.out.println("export task <task-id> <file.ics>");
                System.out.println("export project <project-name> <file.ics>");
                System.out.println("export filtered <file.ics>");
                System.out.println("Exports eligible tasks to iCalendar (.ics) format.");
                System.out.println("Only tasks with a due date are exported.");
                break;

            case "sort-task":
                System.out.println("sort-task <due-date|priority|status|project|tag>");
                System.out.println("Sorts all tasks using the selected mode.");
                System.out.println("Examples: sort-task due-date, sort-task priority");
                break;

            case "help":
                System.out.println("help [command-name]");
                break;

            case "exit":
            case "quit":
                System.out.println(commandName.toLowerCase());
                break;

            default:
                System.out.println("Unknown command '" + commandName + "'. Type 'help' to see available commands.");
                break;
        }
    }
}
