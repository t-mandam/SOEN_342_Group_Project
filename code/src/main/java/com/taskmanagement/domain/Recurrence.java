package com.taskmanagement.domain;

import com.taskmanagement.enums.RecurrenceType;

/**
 * Represents the recurrence information for a task
 */
public class Recurrence {
    private RecurrenceType type;
    private int interval;

    public Recurrence() {}

    public Recurrence(RecurrenceType type, int interval) {
        this.type = type;
        this.interval = interval;
    }

    // Getters and setters
    public RecurrenceType getType() {
        return type;
    }

    public void setType(RecurrenceType type) {
        this.type = type;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}