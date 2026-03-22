package com.taskmanagement.command;

import com.taskmanagement.factory.TaskFactory;

/**
 * Command to create a new task
 */
public class CreateTaskCommand implements Command {
    private TaskFactory taskFactory = new TaskFactory();
    private String title;

    public CreateTaskCommand() {}

    @Override
    public void execute() {
        taskFactory.createTask(title);
    }

    // Set title for the task to be created
    public void setTitle(String title) {
        this.title = title;
    }
}