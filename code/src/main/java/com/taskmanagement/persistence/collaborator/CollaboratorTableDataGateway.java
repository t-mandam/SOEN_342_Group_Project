package com.taskmanagement.persistence.collaborator;

import com.taskmanagement.persistence.DatabaseConnection;

import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.util.SimpleIdGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Data Gateway for Collaborator entities
 * Encapsulates all SQL operations for the collaborators table
 */
public class CollaboratorTableDataGateway {
    private final DatabaseConnection dbConnection;
    private final CollaboratorMapper mapper;

    public CollaboratorTableDataGateway(DatabaseConnection dbConnection, CollaboratorMapper mapper) {
        this.dbConnection = dbConnection;
        this.mapper = mapper;
    }

    /**
     * Inserts a new collaborator into the database
     * @param collaborator the collaborator to insert
     * @return the ID of the inserted collaborator
     * @throws SQLException if database operation fails
     */
    public String insert(Collaborator collaborator) throws SQLException {
        String sql = "INSERT INTO collaborators (id, name, type) VALUES (?, ?, ?)";
        String id = SimpleIdGenerator.nextId();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.setString(2, collaborator.getName());
            stmt.setString(3, getCollaboratorType(collaborator));
            stmt.executeUpdate();
        }

        return id;
    }

    /**
     * Updates an existing collaborator in the database
     * @param id the collaborator ID
     * @param collaborator the updated collaborator
     * @throws SQLException if database operation fails
     */
    public void update(String id, Collaborator collaborator) throws SQLException {
        String sql = "UPDATE collaborators SET name = ?, type = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, collaborator.getName());
            stmt.setString(2, getCollaboratorType(collaborator));
            stmt.setString(3, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds a collaborator by ID
     * @param id the collaborator ID
     * @return the Collaborator if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Collaborator findById(String id) throws SQLException {
        String sql = "SELECT * FROM collaborators WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRowToCollaborator(rs);
                }
            }
        }

        return null;
    }

    /**
     * Finds a collaborator by name
     * @param name the collaborator name
     * @return the Collaborator if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Collaborator findByName(String name) throws SQLException {
        String sql = "SELECT * FROM collaborators WHERE name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRowToCollaborator(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all collaborators from the database
     * @return a list of all collaborators
     * @throws SQLException if database operation fails
     */
    public List<Collaborator> findAll() throws SQLException {
        List<Collaborator> collaborators = new ArrayList<>();
        String sql = "SELECT * FROM collaborators";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                collaborators.add(mapper.mapRowToCollaborator(rs));
            }
        }

        return collaborators;
    }

    /**
     * Finds all collaborators assigned to a specific project
     * @param projectId the ID of the project
     * @return a list of collaborators in the project
     * @throws SQLException if database operation fails
     */
    public List<Collaborator> findByProjectId(String projectId) throws SQLException {
        List<Collaborator> collaborators = new ArrayList<>();
        String sql = "SELECT c.* FROM collaborators c " +
                     "JOIN project_collaborators pc ON c.id = pc.collaborator_id " +
                     "WHERE pc.project_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    collaborators.add(mapper.mapRowToCollaborator(rs));
                }
            }
        }

        return collaborators;
    }

    /**
     * Finds all collaborators assigned to a specific task
     * @param taskId the ID of the task
     * @return a list of collaborators assigned to the task
     * @throws SQLException if database operation fails
     */
    public List<Collaborator> findByTaskId(String taskId) throws SQLException {
        List<Collaborator> collaborators = new ArrayList<>();
        String sql = "SELECT c.* FROM collaborators c " +
                     "JOIN assignments a ON c.id = a.collaborator_id " +
                     "WHERE a.task_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    collaborators.add(mapper.mapRowToCollaborator(rs));
                }
            }
        }

        return collaborators;
    }

    /**
     * Deletes a collaborator from the database
     * @param id the ID of the collaborator to delete
     * @throws SQLException if database operation fails
     */
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM collaborators WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Gets the number of collaborators
     * @return the count of all collaborators
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM collaborators";

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
     * Determines the type string for a collaborator
     * @param collaborator the collaborator to check
     * @return the type as a string
     */
    private String getCollaboratorType(Collaborator collaborator) {
        String className = collaborator.getClass().getSimpleName();
        return className.toUpperCase();
    }
}
