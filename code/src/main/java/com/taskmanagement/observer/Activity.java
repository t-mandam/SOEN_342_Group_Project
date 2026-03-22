package com.taskmanagement.observer;

import java.util.Date;

/**
 * Represents an activity/change in the system
 */
public class Activity {
    private Date timestamp;
    private String description;

    public Activity() {
        this.timestamp = new Date();
    }

    public Activity(String description) {
        this();
        this.description = description;
    }

    // Getters and setters
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + description;
    }
}