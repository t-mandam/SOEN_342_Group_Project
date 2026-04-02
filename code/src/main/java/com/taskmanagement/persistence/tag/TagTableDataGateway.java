package com.taskmanagement.persistence.tag;

import com.taskmanagement.persistence.DatabaseConnection;

import com.taskmanagement.domain.Tag;
import com.taskmanagement.util.SimpleIdGenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Data Gateway for Tag entities
 * Encapsulates all SQL operations for the tags table
 */
public class TagTableDataGateway {
    private final DatabaseConnection dbConnection;
    private final TagMapper mapper;

    public TagTableDataGateway(DatabaseConnection dbConnection, TagMapper mapper) {
        this.dbConnection = dbConnection;
        this.mapper = mapper;
    }

    /**
     * Inserts a new tag into the database
     * @param tag the tag to insert
     * @throws SQLException if database operation fails
     */
    public void insert(Tag tag) throws SQLException {
        String sql = "INSERT INTO tags (id, name) VALUES (?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, SimpleIdGenerator.nextId());
            stmt.setString(2, tag.getName());
            stmt.executeUpdate();
        }
    }

    /**
     * Finds a tag by its name
     * @param name the tag name
     * @return the Tag if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Tag findByName(String name) throws SQLException {
        String sql = "SELECT * FROM tags WHERE name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRowToTag(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all tags from the database
     * @return a list of all tags
     * @throws SQLException if database operation fails
     */
    public List<Tag> findAll() throws SQLException {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT * FROM tags";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tags.add(mapper.mapRowToTag(rs));
            }
        }

        return tags;
    }

    /**
     * Finds all tags associated with a specific task
     * @param taskId the ID of the task
     * @return a list of tags for the task
     * @throws SQLException if database operation fails
     */
    public List<Tag> findByTaskId(String taskId) throws SQLException {
        List<Tag> tags = new ArrayList<>();
        String sql = "SELECT t.* FROM tags t " +
                     "JOIN task_tags tt ON t.id = tt.tag_id " +
                     "WHERE tt.task_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tags.add(mapper.mapRowToTag(rs));
                }
            }
        }

        return tags;
    }

    /**
     * Deletes a tag from the database
     * @param tagName the name of the tag to delete
     * @throws SQLException if database operation fails
     */
    public void deleteByName(String tagName) throws SQLException {
        String sql = "DELETE FROM tags WHERE name = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tagName);
            stmt.executeUpdate();
        }
    }
}
