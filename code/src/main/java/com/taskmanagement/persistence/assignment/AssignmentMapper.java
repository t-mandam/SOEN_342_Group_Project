package com.taskmanagement.persistence.assignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.taskmanagement.util.SimpleIdGenerator;

/**
 * Maps Assignment domain objects to/from database rows
 * Assignments represent the many-to-many relationship between Tasks and Collaborators
 */
public class AssignmentMapper {

    /**
     * Maps a database ResultSet row to assignment values
     * @param rs the ResultSet containing an assignment row
     * @return a map with keys: "id", "task_id", "collaborator_id"
     * @throws SQLException if database access fails
     */
    public Map<String, String> mapRowToAssignment(ResultSet rs) throws SQLException {
        Map<String, String> assignment = new HashMap<>();
        assignment.put("id", rs.getString("id"));
        assignment.put("task_id", rs.getString("task_id"));
        assignment.put("collaborator_id", rs.getString("collaborator_id"));
        return assignment;
    }

    /**
     * Extracts values for an INSERT statement
     * @param taskId the task ID
     * @param collaboratorId the collaborator ID
     * @return array of values: [id, task_id, collaborator_id]
     */
    public Object[] getInsertValues(String taskId, String collaboratorId) {
        return new Object[]{
            SimpleIdGenerator.nextId(),
            taskId,
            collaboratorId
        };
    }
}
