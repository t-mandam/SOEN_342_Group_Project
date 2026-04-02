package com.taskmanagement.search;

import com.taskmanagement.domain.Task;

import java.time.LocalDate;

/**
 * Search criterion that matches tasks with due dates on a specific day of the week
 */
public class DayOfWeekCriterion implements SearchCriterion {
    private int day; // Day of week: 1 = Sunday, 2 = Monday, ..., 7 = Saturday

    public DayOfWeekCriterion() {}

    public DayOfWeekCriterion(int day) {
        this.day = day;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null || task.getDueDate() == null) {
            return false;
        }

        // Validate day range (1-7)
        if (day < 1 || day > 7) {
            return false;
        }

        LocalDate dueDate = task.getDueDate();
        int taskDayOfWeek = toCalendarDayOfWeek(dueDate.getDayOfWeek().getValue());
        return taskDayOfWeek == day;
    }

    private int toCalendarDayOfWeek(int isoDayOfWeek) {
        return isoDayOfWeek == 7 ? 1 : isoDayOfWeek + 1;
    }

    // Getters and setters
    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Convenience methods for setting day of week
     */
    public void setSunday() { this.day = 1; }
    public void setMonday() { this.day = 2; }
    public void setTuesday() { this.day = 3; }
    public void setWednesday() { this.day = 4; }
    public void setThursday() { this.day = 5; }
    public void setFriday() { this.day = 6; }
    public void setSaturday() { this.day = 7; }
}