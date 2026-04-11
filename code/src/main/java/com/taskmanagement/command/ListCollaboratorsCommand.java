package com.taskmanagement.command;

import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.repository.CollaboratorCatalog;
import com.taskmanagement.repository.CollaboratorRepository;

import java.util.List;

/**
 * Command to list all collaborators.
 */
public class ListCollaboratorsCommand implements Command {
    private final CollaboratorRepository collaboratorRepository;

    public ListCollaboratorsCommand() {
        this(CollaboratorCatalog.getInstance());
    }

    public ListCollaboratorsCommand(CollaboratorRepository collaboratorRepository) {
        this.collaboratorRepository = collaboratorRepository;
    }

    @Override
    public void execute() {
        if (collaboratorRepository == null) {
            throw new IllegalStateException("Collaborator repository cannot be null");
        }

        List<Collaborator> collaborators = collaboratorRepository.findAll();
        if (collaborators.isEmpty()) {
            System.out.println("No collaborators found.");
            return;
        }

        System.out.println("Found " + collaborators.size() + " collaborator(s):");

        int nameWidth = 28;
        int levelWidth = 14;

        String divider = "+" + repeat("-", nameWidth + 2)
                + "+" + repeat("-", levelWidth + 2)
                + "+";

        System.out.println(divider);
        System.out.println("| " + pad("Name", nameWidth)
                + " | " + pad("Level", levelWidth)
                + " |");
        System.out.println(divider);

        for (Collaborator collaborator : collaborators) {
            String name = collaborator == null ? "-" : safe(collaborator.getName());
            String level = collaborator == null ? "-" : safe(collaborator.getClass().getSimpleName());

            System.out.println("| " + pad(name, nameWidth)
                    + " | " + pad(level, levelWidth)
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
