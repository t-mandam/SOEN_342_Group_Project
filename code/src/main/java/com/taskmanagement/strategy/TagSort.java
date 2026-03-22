package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Tag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts tasks by the first tag name (alphabetically)
 */
public class TagSort implements SortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);

        sortedTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                String tagName1 = getFirstTagName(t1);
                String tagName2 = getFirstTagName(t2);

                // Tasks without tags go to the end
                if (tagName1 == null && tagName2 == null) {
                    return 0;
                }
                if (tagName1 == null) {
                    return 1;
                }
                if (tagName2 == null) {
                    return -1;
                }

                return tagName1.compareToIgnoreCase(tagName2);
            }
        });

        return sortedTasks;
    }

    private String getFirstTagName(Task task) {
        if (task.getTags() == null || task.getTags().isEmpty()) {
            return null;
        }

        Tag firstTag = task.getTags().get(0);
        return firstTag != null ? firstTag.getName() : null;
    }
}