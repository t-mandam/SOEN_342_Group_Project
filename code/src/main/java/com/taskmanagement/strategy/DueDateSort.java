package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts tasks by due date
 */
public class DueDateSort implements SortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);

        sortedTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                // Handle null due dates - tasks without due dates go to the end
                if (t1.getDueDate() == null && t2.getDueDate() == null) {
                    return 0;
                }
                if (t1.getDueDate() == null) {
                    return 1;
                }
                if (t2.getDueDate() == null) {
                    return -1;
                }
                return t1.getDueDate().compareTo(t2.getDueDate());
            }
        });

        return sortedTasks;
    }
}