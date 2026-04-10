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
        System.out.println("update-task");
        System.out.println("search-task");
        System.out.println("help");
        System.out.println("exit");
        System.out.println("quit");
        System.out.println();
        System.out.println("Type 'help <command-name>' to see command signature and details.");
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

            case "search-task":
                System.out.println("search-task <mode> <args> [| <mode> <args> ...]");
                System.out.println("No args: defaults to status OPEN");
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
