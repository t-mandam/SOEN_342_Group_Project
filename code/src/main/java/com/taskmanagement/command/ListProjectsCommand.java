package com.taskmanagement.command;

import com.taskmanagement.domain.Assignment;
import com.taskmanagement.domain.Project;
import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.AssignmentCatalog;
import com.taskmanagement.repository.AssignmentRepository;
import com.taskmanagement.repository.ProjectCatalog;
import com.taskmanagement.repository.ProjectRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Command to list all projects.
 */
public class ListProjectsCommand implements Command {
    private final ProjectRepository projectRepository;
    private final AssignmentRepository assignmentRepository;

    public ListProjectsCommand() {
        this(ProjectCatalog.getInstance(), AssignmentCatalog.getInstance());
    }

    public ListProjectsCommand(ProjectRepository projectRepository) {
        this(projectRepository, AssignmentCatalog.getInstance());
    }

    public ListProjectsCommand(ProjectRepository projectRepository, AssignmentRepository assignmentRepository) {
        this.projectRepository = projectRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public void execute() {
        if (projectRepository == null) {
            throw new IllegalStateException("Project repository cannot be null");
        }
        if (assignmentRepository == null) {
            throw new IllegalStateException("Assignment repository cannot be null");
        }

        List<Project> projects = projectRepository.findAll();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("Found " + projects.size() + " project(s):");

        int nameWidth = 24;
        int descriptionWidth = 40;
        int tasksWidth = 7;
        int collaboratorsWidth = 13;

        String divider = "+" + repeat("-", nameWidth + 2)
                + "+" + repeat("-", descriptionWidth + 2)
                + "+" + repeat("-", tasksWidth + 2)
                + "+" + repeat("-", collaboratorsWidth + 2)
                + "+";

        System.out.println(divider);
        System.out.println("| " + pad("Project", nameWidth)
                + " | " + pad("Description", descriptionWidth)
                + " | " + pad("Tasks", tasksWidth)
                + " | " + pad("Collaborators", collaboratorsWidth)
                + " |");
        System.out.println(divider);

        for (Project project : projects) {
            String name = safe(project.getName());
            String description = safe(project.getDescription());
            int tasksCount = project.getTasks() == null ? 0 : project.getTasks().size();
            int collaboratorsCount = countUniqueCollaboratorsForProject(project);

            System.out.println("| " + pad(name, nameWidth)
                    + " | " + pad(description, descriptionWidth)
                    + " | " + pad(String.valueOf(tasksCount), tasksWidth)
                    + " | " + pad(String.valueOf(collaboratorsCount), collaboratorsWidth)
                    + " |");
        }

        System.out.println(divider);
    }

    private int countUniqueCollaboratorsForProject(Project project) {
        if (project == null || project.getTasks() == null || project.getTasks().isEmpty()) {
            return 0;
        }

        Set<String> collaboratorNames = new HashSet<>();
        for (Task task : project.getTasks()) {
            if (task == null || task.getId() == null || task.getId().trim().isEmpty()) {
                continue;
            }

            List<Assignment> assignments = assignmentRepository.findByTaskId(task.getId().trim());
            for (Assignment assignment : assignments) {
                if (assignment != null
                        && assignment.getCollaborator() != null
                        && assignment.getCollaborator().getName() != null
                        && !assignment.getCollaborator().getName().trim().isEmpty()) {
                    collaboratorNames.add(assignment.getCollaborator().getName().trim().toLowerCase());
                }
            }
        }

        return collaboratorNames.size();
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
