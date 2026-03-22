package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Priority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts tasks by priority (HIGH -> MEDIUM -> LOW)
 */
public class PrioritySort implements SortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);

        sortedTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                Priority p1 = t1.getPriority();
                Priority p2 = t2.getPriority();

                // Handle null priorities - tasks without priority go to the end
                if (p1 == null && p2 == null) {
                    return 0;
                }
                if (p1 == null) {
                    return 1;
                }
                if (p2 == null) {
                    return -1;
                }

                // Sort HIGH -> MEDIUM -> LOW
                return getPriorityValue(p1) - getPriorityValue(p2);
            }
        });

        return sortedTasks;
    }

    private int getPriorityValue(Priority priority) {
        switch (priority) {
            case HIGH:
                return 1;
            case MEDIUM:
                return 2;
            case LOW:
                return 3;
            default:
                return 4;
        }
    }
}