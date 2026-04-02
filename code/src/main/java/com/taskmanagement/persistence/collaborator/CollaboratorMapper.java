package com.taskmanagement.persistence.collaborator;

import com.taskmanagement.domain.Collaborator;
import com.taskmanagement.domain.Junior;
import com.taskmanagement.domain.Intermediate;
import com.taskmanagement.domain.Senior;
import com.taskmanagement.util.SimpleIdGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps Collaborator domain objects to/from database rows
 * Handles polymorphic mapping of Junior, Intermediate, and Senior collaborators
 */
public class CollaboratorMapper {

    /**
     * Maps a database ResultSet row to a Collaborator object
     * Creates the appropriate subclass (Junior, Intermediate, Senior) based on type
     * @param rs the ResultSet containing a collaborator row
     * @return a Collaborator object (Junior, Intermediate, or Senior)
     * @throws SQLException if database access fails
     */
    public Collaborator mapRowToCollaborator(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        String type = rs.getString("type");

        // Create appropriate subclass based on type
        switch (type.toUpperCase()) {
            case "JUNIOR":
                return new Junior(name);
            case "INTERMEDIATE":
                return new Intermediate(name);
            case "SENIOR":
                return new Senior(name);
            default:
                throw new SQLException("Unknown collaborator type: " + type);
        }
    }

    /**
     * Maps a Collaborator object to database column values
     * @param collaborator the Collaborator to map
     * @return a map of column names to values
     */
    public Map<String, Object> mapCollaboratorToValues(Collaborator collaborator) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", collaborator.getName());
        values.put("type", getCollaboratorType(collaborator));
        return values;
    }

    /**
     * Extracts column values from a Collaborator for an INSERT statement
     * @param collaborator the Collaborator to extract values from
     * @return array of values: [id, name, type]
     */
    public Object[] getInsertValues(Collaborator collaborator) {
        return new Object[]{
            SimpleIdGenerator.nextId(),
            collaborator.getName(),
            getCollaboratorType(collaborator)
        };
    }

    /**
     * Extracts column values from a Collaborator for an UPDATE statement
     * @param collaborator the Collaborator to extract values from
     * @return array of values: [name, type, id]
     */
    public Object[] getUpdateValues(Collaborator collaborator, String id) {
        return new Object[]{
            collaborator.getName(),
            getCollaboratorType(collaborator),
            id
        };
    }

    /**
     * Determines the type string for a collaborator
     * @param collaborator the collaborator to check
     * @return the type as a string: "JUNIOR", "INTERMEDIATE", or "SENIOR"
     */
    private String getCollaboratorType(Collaborator collaborator) {
        if (collaborator instanceof Junior) {
            return "JUNIOR";
        } else if (collaborator instanceof Intermediate) {
            return "INTERMEDIATE";
        } else if (collaborator instanceof Senior) {
            return "SENIOR";
        } else {
            throw new IllegalArgumentException("Unknown collaborator type: " + collaborator.getClass().getName());
        }
    }
}
