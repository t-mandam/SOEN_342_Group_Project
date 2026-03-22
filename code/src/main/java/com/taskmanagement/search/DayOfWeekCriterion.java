package com.taskmanagement.search;

import com.taskmanagement.domain.Task;

import java.util.Calendar;
import java.util.Date;

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

        Date dueDate = task.getDueDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueDate);

        int taskDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return taskDayOfWeek == day;
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
    public void setSunday() { this.day = Calendar.SUNDAY; }
    public void setMonday() { this.day = Calendar.MONDAY; }
    public void setTuesday() { this.day = Calendar.TUESDAY; }
    public void setWednesday() { this.day = Calendar.WEDNESDAY; }
    public void setThursday() { this.day = Calendar.THURSDAY; }
    public void setFriday() { this.day = Calendar.FRIDAY; }
    public void setSaturday() { this.day = Calendar.SATURDAY; }
}