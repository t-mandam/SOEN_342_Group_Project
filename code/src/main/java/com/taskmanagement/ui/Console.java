package com.taskmanagement.ui;

import com.taskmanagement.command.*;
import com.taskmanagement.factory.TaskFactory;
import com.taskmanagement.repository.TaskCatalog;

import java.util.Scanner;

public class Console {
    private final TaskCatalog taskCatalog;
    private final TaskFactory taskFactory;

    public Console() {
        this.taskCatalog = TaskCatalog.getInstance();
        this.taskFactory = new TaskFactory(taskCatalog);
    }

    public void executeCommand(Command command) {
        command.execute();
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

                // ADD COMMANDS HERE
                default:
                    System.out.println("Unknown command. Type 'help' to see available commands.");
                    break;
            }

            executeCommand(command);
        } catch (RuntimeException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void printHelp() {
        System.out.println("Type 'help' for commands.");
    }

    public static void main(String[] args) {
        new Console().start();
    }
}