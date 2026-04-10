package com.taskmanagement.command;

import com.taskmanagement.domain.Assignment;
import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.domain.Task;

/**
 * Command to assign a collaborator to a task
 */
public class AssignCollaboratorCommand implements Command {
    private Assignment assignment;
    private Task task;
    private Collaborator collaborator;

    public AssignCollaboratorCommand() {}

    public AssignCollaboratorCommand(Assignment assignment, Task task, Collaborator collaborator) {
        this.assignment = assignment;
        this.task = task;
        this.collaborator = collaborator;
    }

    @Override
    public void execute() {
        if (assignment == null) {
            throw new IllegalStateException("Assignment cannot be null");
        }
        if (task == null) {
            throw new IllegalStateException("Task cannot be null");
        }
        if (collaborator == null) {
            throw new IllegalStateException("Collaborator cannot be null");
        }

        assignment.setTask(task);
        assignment.setCollaborator(collaborator);
    }

    // Getters and setters
    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }
}