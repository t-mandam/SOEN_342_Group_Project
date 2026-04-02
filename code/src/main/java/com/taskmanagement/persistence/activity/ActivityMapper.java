package com.taskmanagement.persistence.activity;

import com.taskmanagement.observer.Activity;
import com.taskmanagement.util.SimpleIdGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps Activity domain objects to/from database rows
 */
public class ActivityMapper {

    /**
     * Maps a database ResultSet row to an Activity object
     * @param rs the ResultSet containing an activity row
     * @return an Activity object populated with data from the ResultSet
     * @throws SQLException if database access fails
     */
    public Activity mapRowToActivity(ResultSet rs) throws SQLException {
        Activity activity = new Activity();

        Timestamp timestamp = rs.getTimestamp("timestamp");
        if (timestamp != null) {
            activity.setTimestamp(new Date(timestamp.getTime()));
        }

        String description = rs.getString("description");
        if (description != null) {
            activity.setDescription(description);
        }

        return activity;
    }

    /**
     * Maps an Activity object to database column values
     * @param activity the Activity to map
     * @return a map of column names to values
     */
    public Map<String, Object> mapActivityToValues(Activity activity) {
        Map<String, Object> values = new HashMap<>();
        values.put("timestamp", activity.getTimestamp() != null ? new Timestamp(activity.getTimestamp().getTime()) : new Timestamp(System.currentTimeMillis()));
        values.put("description", activity.getDescription());
        return values;
    }

    /**
     * Extracts column values from an Activity for an INSERT statement
     * @param activity the Activity to extract values from
     * @return array of values: [id, timestamp, description]
     */
    public Object[] getInsertValues(Activity activity) {
        return new Object[]{
            SimpleIdGenerator.nextId(),
            activity.getTimestamp() != null ? new Timestamp(activity.getTimestamp().getTime()) : new Timestamp(System.currentTimeMillis()),
            activity.getDescription()
        };
    }
}
