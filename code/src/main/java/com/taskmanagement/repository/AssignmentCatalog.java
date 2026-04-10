package com.taskmanagement.repository;

import com.taskmanagement.domain.Assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory singleton implementation of AssignmentRepository.
 */
public class AssignmentCatalog implements AssignmentRepository {
    private static final AssignmentCatalog INSTANCE = new AssignmentCatalog();
    private final List<Assignment> assignmentCatalog;

    private AssignmentCatalog() {
        this.assignmentCatalog = new ArrayList<>();
    }

    public static AssignmentCatalog getInstance() {
        return INSTANCE;
    }

    @Override
    public void addAssignment(Assignment assignment) {
        validateAssignment(assignment);
        if (findByTaskId(assignment.getTask().getId()) != null) {
            throw new IllegalArgumentException("Task already has an assignment: " + assignment.getTask().getId());
        }
        assignmentCatalog.add(assignment);
    }

    @Override
    public void updateAssignment(Assignment assignment) {
        validateAssignment(assignment);
        String taskId = assignment.getTask().getId();

        for (int i = 0; i < assignmentCatalog.size(); i++) {
            Assignment existing = assignmentCatalog.get(i);
            if (hasSameTaskId(existing, taskId)) {
                assignmentCatalog.set(i, assignment);
                return;
            }
        }

        throw new IllegalArgumentException("Assignment not found for task ID: " + taskId);
    }

    @Override
    public void removeAssignment(Assignment assignment) {
        if (assignment != null) {
            assignmentCatalog.remove(assignment);
        }
    }

    @Override
    public Assignment findByTaskId(String taskId) {
        if (taskId == null || taskId.trim().isEmpty()) {
            return null;
        }

        for (Assignment assignment : assignmentCatalog) {
            if (hasSameTaskId(assignment, taskId)) {
                return assignment;
            }
        }

        return null;
    }

    @Override
    public List<Assignment> findByCollaboratorName(String collaboratorName) {
        List<Assignment> matches = new ArrayList<>();
        if (collaboratorName == null || collaboratorName.trim().isEmpty()) {
            return matches;
        }

        String normalized = collaboratorName.trim().toLowerCase();
        for (Assignment assignment : assignmentCatalog) {
            if (assignment != null
                    && assignment.getCollaborator() != null
                    && assignment.getCollaborator().getName() != null
                    && assignment.getCollaborator().getName().trim().toLowerCase().equals(normalized)) {
                matches.add(assignment);
            }
        }

        return matches;
    }

    @Override
    public List<Assignment> findAll() {
        return new ArrayList<>(assignmentCatalog);
    }

    public int size() {
        return assignmentCatalog.size();
    }

    private void validateAssignment(Assignment assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Assignment cannot be null");
        }
        if (assignment.getTask() == null || assignment.getTask().getId() == null || assignment.getTask().getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment task ID cannot be null or empty");
        }
        if (assignment.getCollaborator() == null || assignment.getCollaborator().getName() == null || assignment.getCollaborator().getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Assignment collaborator name cannot be null or empty");
        }
    }

    private boolean hasSameTaskId(Assignment assignment, String taskId) {
        return assignment != null
                && assignment.getTask() != null
                && assignment.getTask().getId() != null
                && assignment.getTask().getId().equals(taskId.trim());
    }
}
