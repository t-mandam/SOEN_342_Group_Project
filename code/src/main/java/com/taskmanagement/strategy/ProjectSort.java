package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts tasks by project name (alphabetically)
 * Note: This is a placeholder implementation since the relationship between Task and Project
 * would need to be established through a service or repository layer
 */
public class ProjectSort implements SortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);

        // For now, we'll sort by task title as a placeholder
        // In a complete implementation, this would sort by the project name that contains the task
        sortedTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                String title1 = t1.getTitle();
                String title2 = t2.getTitle();

                if (title1 == null && title2 == null) {
                    return 0;
                }
                if (title1 == null) {
                    return 1;
                }
                if (title2 == null) {
                    return -1;
                }

                return title1.compareToIgnoreCase(title2);
            }
        });

        return sortedTasks;
    }
}