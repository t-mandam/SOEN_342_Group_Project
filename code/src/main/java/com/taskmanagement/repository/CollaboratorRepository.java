package com.taskmanagement.repository;

import com.taskmanagement.domain.Collaborator;

import java.util.List;

/**
 * Repository interface for collaborator operations.
 */
public interface CollaboratorRepository {
    void addCollaborator(Collaborator collaborator);

    void updateCollaborator(Collaborator collaborator);

    void removeCollaborator(Collaborator collaborator);

    Collaborator findByName(String name);

    List<Collaborator> findAll();
}
