package com.taskmanagement.command;

import com.taskmanagement.domain.Assignment;
import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.AssignmentRepository;

import java.util.List;

/**
 * Command to list all task assignments.
 */
public class ListAssignmentsCommand implements Command {
    private final AssignmentRepository assignmentRepository;

    public ListAssignmentsCommand() {
        this(AssignmentCatalog.getInstance());
    }

    public ListAssignmentsCommand(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void execute() {
        if (assignmentRepository == null) {
            throw new IllegalStateException("Assignment repository cannot be null");
        }

        List<Assignment> assignments = assignmentRepository.findAll();
        if (assignments.isEmpty()) {
            System.out.println("No assignments found.");
            return;
        }

        System.out.println("Found " + assignments.size() + " assignment(s):");

        int taskIdWidth = 10;
        int taskTitleWidth = 32;
        int collaboratorWidth = 24;

        String divider = "+" + repeat("-", taskIdWidth + 2)
                + "+" + repeat("-", taskTitleWidth + 2)
                + "+" + repeat("-", collaboratorWidth + 2)
                + "+";

        System.out.println(divider);
        System.out.println("| " + pad("Task ID", taskIdWidth)
                + " | " + pad("Task Title", taskTitleWidth)
                + " | " + pad("Collaborator", collaboratorWidth)
                + " |");
        System.out.println(divider);

        for (Assignment assignment : assignments) {
            Task task = assignment == null ? null : assignment.getTask();
            Collaborator collaborator = assignment == null ? null : assignment.getCollaborator();

            String taskId = task == null ? "-" : safe(task.getId());
            String taskTitle = task == null ? "-" : safe(task.getTitle());
            String collaboratorName = collaborator == null ? "-" : safe(collaborator.getName());

            System.out.println("| " + pad(taskId, taskIdWidth)
                    + " | " + pad(taskTitle, taskTitleWidth)
                    + " | " + pad(collaboratorName, collaboratorWidth)
                    + " |");
        }

        System.out.println(divider);
    }

    private String safe(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value;
    }

    private String pad(String value, int width) {
        String text = value == null ? "-" : value;
        if (text.length() > width) {
            if (width <= 1) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 1) + "~";
        }
        return text + repeat(" ", width - text.length());
    }

    private String repeat(String text, int count) {
        if (count <= 0) {
            return "";
        }
        return String.valueOf(text).repeat(count);
    }
}
