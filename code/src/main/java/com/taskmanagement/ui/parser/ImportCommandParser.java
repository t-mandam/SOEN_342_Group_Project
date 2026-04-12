package com.taskmanagement.ui.parser;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.ImportCommand;

/**
 * Parses CLI arguments for import command.
 */
public class ImportCommandParser {

    public Command parse(String args) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException("Usage: import <csv-file-path>");
        }

        String importSource = args.trim();
        return new ImportCommand(importSource);
    }
}
