package com.taskmanagement.search;

import com.taskmanagement.domain.Task;

import java.util.Date;

/**
 * Search criterion that matches tasks with due dates within a specified range
 */
public class DateRangeCriterion implements SearchCriterion {
    private Date fromDate;
    private Date toDate;

    public DateRangeCriterion() {}

    public DateRangeCriterion(Date fromDate, Date toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null || task.getDueDate() == null) {
            return false;
        }

        Date dueDate = task.getDueDate();

        // Check if due date is within the range
        boolean afterFromDate = (fromDate == null) || (!dueDate.before(fromDate));
        boolean beforeToDate = (toDate == null) || (!dueDate.after(toDate));

        return afterFromDate && beforeToDate;
    }

    // Getters and setters
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }
}