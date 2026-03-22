package com.taskmanagement.ui;

import com.taskmanagement.command.Command;

public class Console {

    public Console() {
    }

    public void executeCommand(Command command) {
        command.execute();
    }
}