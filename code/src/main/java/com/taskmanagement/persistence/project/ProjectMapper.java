package com.taskmanagement.persistence.project;

import com.taskmanagement.domain.Project;
import com.taskmanagement.util.SimpleIdGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps Project domain objects to/from database rows
 */
public class ProjectMapper {

    /**
     * Maps a database ResultSet row to a Project object
     * @param rs the ResultSet containing a project row
     * @return a Project object populated with data from the ResultSet
     * @throws SQLException if database access fails
     */
    public Project mapRowToProject(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setName(rs.getString("name"));
        project.setDescription(rs.getString("description"));
        return project;
    }

    /**
     * Maps a Project object to database column values
     * @param project the Project to map
     * @return a map of column names to values
     */
    public Map<String, Object> mapProjectToValues(Project project) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", project.getName());
        values.put("description", project.getDescription());
        return values;
    }

    /**
     * Extracts column values from a Project for an INSERT statement
     * @param project the Project to extract values from
     * @return array of values: [id, name, description]
     */
    public Object[] getInsertValues(Project project) {
        return new Object[]{
            SimpleIdGenerator.nextId(),
            project.getName(),
            project.getDescription()
        };
    }

    /**
     * Extracts column values from a Project for an UPDATE statement
     * @param project the Project to extract values from
     * @return array of values: [name, description, id]
     */
    public Object[] getUpdateValues(Project project, String id) {
        return new Object[]{
            project.getName(),
            project.getDescription(),
            id
        };
    }
}
