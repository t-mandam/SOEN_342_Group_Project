package com.taskmanagement.search;

import com.taskmanagement.domain.Task;

import java.time.LocalDate;

/**
 * Search criterion that matches tasks with due dates within a specified range
 */
public class DateRangeCriterion implements SearchCriterion {
    private LocalDate fromDate;
    private LocalDate toDate;

    public DateRangeCriterion() {}

    public DateRangeCriterion(LocalDate fromDate, LocalDate toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null || task.getDueDate() == null) {
            return false;
        }

        LocalDate dueDate = task.getDueDate();

        // Check if due date is within the range
        boolean afterFromDate = (fromDate == null) || (!dueDate.isBefore(fromDate));
        boolean beforeToDate = (toDate == null) || (!dueDate.isAfter(toDate));

        return afterFromDate && beforeToDate;
    }

    // Getters and setters
    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}