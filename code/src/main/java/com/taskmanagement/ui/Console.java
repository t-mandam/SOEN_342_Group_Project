package com.taskmanagement.ui;

import com.taskmanagement.command.*;
import java.util.Scanner;

public class Console {
    private final CreateTaskCommandParser createTaskCommandParser;
    private final UpdateTaskCommandParser updateTaskCommandParser;
    private final SearchTaskCommandParser searchTaskCommandParser;

    public Console() {
        this.createTaskCommandParser = new CreateTaskCommandParser();
        this.updateTaskCommandParser = new UpdateTaskCommandParser();
        this.searchTaskCommandParser = new SearchTaskCommandParser();
    }

    public void executeCommand(Command command) {
        if (command != null) {
            command.execute();
        }
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Personal Task Management CLI");
        printHelp();

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
                printHelp();
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

                case "search-task":
                    command = parseSearchTask(args);
                    break;

                case "help":
                    printHelp(args);
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

    private void printHelp() {
        System.out.println("\n=== Available Commands ===");
        System.out.println("create-task");
        System.out.println("update-task");
        System.out.println("search-task");
        System.out.println("help");
        System.out.println("exit");
        System.out.println("quit");
        System.out.println();
        System.out.println("Type 'help <command-name>' to see command signature and details.");
        System.out.println();
    }

    private void printHelp(String commandName) {
        if (commandName == null || commandName.trim().isEmpty()) {
            printHelp();
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
                break;

            case "search-task":
                System.out.println("search-task <mode> <args> [| <mode> <args> ...]");
                System.out.println("No args: defaults to status OPEN");
                System.out.println("Modes:");
                System.out.println("  keyword <text>");
                System.out.println("  tag <tag-name>");
                System.out.println("  status <OPEN|COMPLETED|CANCELLED>");
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

    public static void main(String[] args) {
        new Console().start();
    }
}