package com.taskmanagement.repository;

import com.taskmanagement.domain.Collaborator;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory singleton implementation of CollaboratorRepository.
 */
public class CollaboratorCatalog implements CollaboratorRepository {
    private static final CollaboratorCatalog INSTANCE = new CollaboratorCatalog();
    private final List<Collaborator> collaboratorCatalog;

    private CollaboratorCatalog() {
        this.collaboratorCatalog = new ArrayList<>();
    }

    public static CollaboratorCatalog getInstance() {
        return INSTANCE;
    }

    @Override
    public void addCollaborator(Collaborator collaborator) {
        if (collaborator == null || collaborator.getName() == null || collaborator.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Collaborator name cannot be null or empty");
        }
        if (findByName(collaborator.getName()) != null) {
            throw new IllegalArgumentException("Collaborator already exists: " + collaborator.getName());
        }

        collaboratorCatalog.add(collaborator);
    }

    @Override
    public void updateCollaborator(Collaborator collaborator) {
        if (collaborator == null || collaborator.getName() == null || collaborator.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Collaborator name cannot be null or empty");
        }

        String normalizedName = collaborator.getName().trim();
        for (int i = 0; i < collaboratorCatalog.size(); i++) {
            Collaborator existing = collaboratorCatalog.get(i);
            if (existing.getName() != null && existing.getName().trim().equalsIgnoreCase(normalizedName)) {
                collaboratorCatalog.set(i, collaborator);
                return;
            }
        }

        throw new IllegalArgumentException("Collaborator not found: " + collaborator.getName());
    }

    @Override
    public void removeCollaborator(Collaborator collaborator) {
        if (collaborator != null) {
            collaboratorCatalog.remove(collaborator);
        }
    }

    @Override
    public Collaborator findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String normalizedName = name.trim().toLowerCase();
        for (Collaborator collaborator : collaboratorCatalog) {
            if (collaborator.getName() != null && collaborator.getName().trim().toLowerCase().equals(normalizedName)) {
                return collaborator;
            }
        }

        return null;
    }

    @Override
    public List<Collaborator> findAll() {
        return new ArrayList<>(collaboratorCatalog);
    }

    public int size() {
        return collaboratorCatalog.size();
    }
}
