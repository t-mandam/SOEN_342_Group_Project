package com.taskmanagement.repository;

import com.taskmanagement.domain.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory singleton implementation of ProjectRepository.
 */
public class ProjectCatalog implements ProjectRepository {
    private static final ProjectCatalog INSTANCE = new ProjectCatalog();
    private final List<Project> projectCatalog;

    private ProjectCatalog() {
        this.projectCatalog = new ArrayList<>();
    }

    public static ProjectCatalog getInstance() {
        return INSTANCE;
    }

    @Override
    public void addProject(Project project) {
        if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        if (findByName(project.getName()) != null) {
            throw new IllegalArgumentException("Project already exists: " + project.getName());
        }

        projectCatalog.add(project);
    }

    @Override
    public void updateProject(Project project) {
        if (project == null || project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }

        String normalizedName = project.getName().trim();
        for (int i = 0; i < projectCatalog.size(); i++) {
            Project existingProject = projectCatalog.get(i);
            if (existingProject.getName() != null && existingProject.getName().trim().equalsIgnoreCase(normalizedName)) {
                projectCatalog.set(i, project);
                return;
            }
        }

        throw new IllegalArgumentException("Project not found: " + project.getName());
    }

    @Override
    public void removeProject(Project project) {
        if (project != null) {
            projectCatalog.remove(project);
        }
    }

    @Override
    public Project findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String normalizedName = name.trim().toLowerCase();
        for (Project project : projectCatalog) {
            if (project.getName() != null && project.getName().trim().toLowerCase().equals(normalizedName)) {
                return project;
            }
        }

        return null;
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<>(projectCatalog);
    }

    public int size() {
        return projectCatalog.size();
    }
}
