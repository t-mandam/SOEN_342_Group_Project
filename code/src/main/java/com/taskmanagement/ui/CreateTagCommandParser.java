package com.taskmanagement.ui;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.CreateTagCommand;

/**
 * Parses CLI arguments for create-tag command.
 */
public class CreateTagCommandParser {

    public Command parse(String args) {
        if (args == null || args.trim().isEmpty()) {
            throw new IllegalArgumentException("Usage: create-tag <tag-name>");
        }

        return new CreateTagCommand(args.trim());
    }
}
