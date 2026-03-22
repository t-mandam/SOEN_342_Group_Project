package com.taskmanagement.observer;

import com.taskmanagement.domain.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TaskObserver that logs task activities
 */
public class ActivityLog implements TaskObserver {
    private List<Activity> updates;

    public ActivityLog() {
        this.updates = new ArrayList<>();
    }

    @Override
    public void update(Task task) {
        String description = "Task '" + task.getTitle() + "' was updated. Status: " + task.getStatus();
        Activity activity = new Activity(description);
        updates.add(activity);
    }

    /**
     * Gets all logged activities
     * @return list of activities
     */
    public List<Activity> getUpdates() {
        return new ArrayList<>(updates);
    }

    /**
     * Adds a custom activity to the log
     * @param activity the activity to add
     */
    public void addActivity(Activity activity) {
        updates.add(activity);
    }

    /**
     * Clears all logged activities
     */
    public void clearLog() {
        updates.clear();
    }

    /**
     * Gets the number of logged activities
     * @return the number of activities
     */
    public int getActivitiesCount() {
        return updates.size();
    }
}