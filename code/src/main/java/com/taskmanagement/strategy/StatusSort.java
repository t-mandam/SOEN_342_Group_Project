package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;
import com.taskmanagement.enums.Status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts tasks by status (OPEN -> COMPLETED -> CANCELLED)
 */
public class StatusSort implements SortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);

        sortedTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                Status s1 = t1.getStatus();
                Status s2 = t2.getStatus();

                // Handle null status - should not happen but just in case
                if (s1 == null && s2 == null) {
                    return 0;
                }
                if (s1 == null) {
                    return 1;
                }
                if (s2 == null) {
                    return -1;
                }

                // Sort OPEN -> COMPLETED -> CANCELLED
                return getStatusValue(s1) - getStatusValue(s2);
            }
        });

        return sortedTasks;
    }

    private int getStatusValue(Status status) {
        switch (status) {
            case OPEN:
                return 1;
            case COMPLETED:
                return 2;
            case CANCELLED:
                return 3;
            default:
                return 4;
        }
    }
}