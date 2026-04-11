package com.taskmanagement.persistence.task;

import com.taskmanagement.domain.Task;
import com.taskmanagement.persistence.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Table Data Gateway for Task entities
 * Encapsulates all SQL operations for the tasks table
 */
public class TaskTableDataGateway {
    private final DatabaseConnection dbConnection;
    private final TaskMapper mapper;

    public TaskTableDataGateway(DatabaseConnection dbConnection, TaskMapper mapper) {
        this.dbConnection = dbConnection;
        this.mapper = mapper;
    }

    /**
     * Inserts a new task into the database
     * @param task the task to insert
     * @throws SQLException if database operation fails
     */
    public void insert(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (id, title, description, creation_date, due_date, priority, status, recurrence_type, recurrence_interval, parent_task_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Object[] values = mapper.getInsertValues(task);
            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing task in the database
     * @param task the task to update
     * @throws SQLException if database operation fails
     */
    public void update(Task task) throws SQLException {
        String sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, priority = ?, status = ?, " +
                     "recurrence_type = ?, recurrence_interval = ?, parent_task_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Object[] values = mapper.getUpdateValues(task);
            for (int i = 0; i < values.length; i++) {
                stmt.setObject(i + 1, values[i]);
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a task from the database
     * @param taskId the ID of the task to delete
     * @throws SQLException if database operation fails
     */
    public void delete(String taskId) throws SQLException {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taskId);
            stmt.executeUpdate();
        }
    }

    /**
     * Finds a task by its ID
     * @param taskId the ID of the task to find
     * @return the Task if found, null otherwise
     * @throws SQLException if database operation fails
     */
    public Task findById(String taskId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taskId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.mapRowToTask(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all tasks from the database
     * @return a list of all tasks
     * @throws SQLException if database operation fails
     */
    public List<Task> findAll() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tasks.add(mapper.mapRowToTask(rs));
            }
        }

        return tasks;
    }

    /**
     * Finds all tasks in a specific project
     * @param projectId the ID of the project
     * @return a list of tasks in the project
     * @throws SQLException if database operation fails
     */
    public List<Task> findByProjectId(String projectId) throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE project_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapper.mapRowToTask(rs));
                }
            }
        }

        return tasks;
    }

    /**
     * Gets the count of all tasks in the database
     * @return the number of tasks
     * @throws SQLException if database operation fails
     */
    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks";

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
     * Assigns a task to a project
     * @param taskId the ID of the task
     * @param projectId the ID of the project
     * @throws SQLException if database operation fails
     */
    public void assignToProject(String taskId, String projectId) throws SQLException {
        String sql = "UPDATE tasks SET project_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projectId);
            stmt.setString(2, taskId);
            stmt.executeUpdate();
        }
    }

    /**
     * Removes a task from its project
     * @param taskId the ID of the task
     * @throws SQLException if database operation fails
     */
    public void removeFromProject(String taskId) throws SQLException {
        String sql = "UPDATE tasks SET project_id = NULL WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, taskId);
            stmt.executeUpdate();
        }
    }
}
