package com.taskmanagement.ui;

import com.taskmanagement.command.*;
import java.util.Scanner;

public class Console {
    private final CreateTaskCommandParser createTaskCommandParser;
    private final CreateTagCommandParser createTagCommandParser;
    private final UpdateTaskCommandParser updateTaskCommandParser;
    private final SearchTaskCommandParser searchTaskCommandParser;

    public Console() {
        this.createTaskCommandParser = new CreateTaskCommandParser();
        this.createTagCommandParser = new CreateTagCommandParser();
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

                case "search-task":
                    command = parseSearchTask(args);
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

    public static void main(String[] args) {
        new Console().start();
    }
}