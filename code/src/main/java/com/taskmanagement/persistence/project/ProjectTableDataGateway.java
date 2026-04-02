package com.taskmanagement.persistence.project;

import com.taskmanagement.persistence.DatabaseConnection;

import com.taskmanagement.domain.Project;
import com.taskmanagement.util.SimpleIdGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Data Gateway for Project entities
 * Encapsulates all SQL operations for the projects table
 */
public class ProjectTableDataGateway {
    private final DatabaseConnection dbConnection;
    private final ProjectMapper mapper;

    public ProjectTableDataGateway(DatabaseConnection dbConnection, ProjectMapper mapper) {
        this.dbConnection = dbConnection;
        this.mapper = mapper;
    }

    /**
     * Inserts a new project into the database
     * @param project the project to insert
     * @return the ID of the inserted project
     * @throws SQLException if database operation fails
     */
    public String insert(Project project) throws SQLException {
        String sql = "INSERT INTO projects (id, name, description) VALUES (?, ?, ?)";
        String id = SimpleIdGenerator.nextId();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.setString(2, project.getName());
            stmt.setString(3, project.getDescription());
            stmt.executeUpdate();
        }

        return id;
    }

    /**
     * Updates an existing project in the database
     * @param id the project ID
     * @param project the updated project
     * @throws SQLException if database operation fails
     */
    public void update(String id, Project project) throws SQLException {
        String sql = "UPDATE projects SET name = ?, description = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds a project by ID
     * @param id the project ID
     * @return the Project if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Project findById(String id) throws SQLException {
        String sql = "SELECT * FROM projects WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRowToProject(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds a project by name
     * @param name the project name
     * @return the Project if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Project findByName(String name) throws SQLException {
        String sql = "SELECT * FROM projects WHERE name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRowToProject(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all projects from the database
     * @return a list of all projects
     * @throws SQLException if database operation fails
     */
    public List<Project> findAll() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                projects.add(mapper.mapRowToProject(rs));
            }
        }

        return projects;
    }

    /**
     * Deletes a project from the database
     * @param id the ID of the project to delete
     * @throws SQLException if database operation fails
     */
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM projects WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Gets the number of projects
     * @return the count of all projects
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM projects";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    /**
     * Adds a collaborator to a project
     * @param projectId the project ID
     * @param collaboratorId the collaborator ID
     * @throws SQLException if database operation fails
     */
    public void addCollaborator(String projectId, String collaboratorId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO project_collaborators (project_id, collaborator_id) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projectId);
            stmt.setString(2, collaboratorId);
            stmt.executeUpdate();
        }
    }

    /**
     * Removes a collaborator from a project
     * @param projectId the project ID
     * @param collaboratorId the collaborator ID
     * @throws SQLException if database operation fails
     */
    public void removeCollaborator(String projectId, String collaboratorId) throws SQLException {
        String sql = "DELETE FROM project_collaborators WHERE project_id = ? AND collaborator_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projectId);
            stmt.setString(2, collaboratorId);
            stmt.executeUpdate();
        }
    }
}
