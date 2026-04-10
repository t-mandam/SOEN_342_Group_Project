package com.taskmanagement.repository;

import com.taskmanagement.domain.Project;

import java.util.List;

/**
 * Repository interface for project operations.
 */
public interface ProjectRepository {
    void addProject(Project project);

    void updateProject(Project project);

    void removeProject(Project project);

    Project findByName(String name);

    List<Project> findAll();
}
