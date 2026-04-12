package com.taskmanagement.ui.parser;

import com.taskmanagement.command.Command;
import com.taskmanagement.command.ListAssignmentsCommand;

/**
 * Parses CLI arguments for list-assignments command.
 */
public class ListAssignmentsCommandParser {

    public Command parse(String args) {
        if (args != null && !args.trim().isEmpty()) {
            throw new IllegalArgumentException("Usage: list-assignments");
        }

        return new ListAssignmentsCommand();
    }
}
