package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Sorts tasks by project name (alphabetically).
 */
public class ProjectSort implements SortStrategy {

    @Override
    public List<Task> sort(List<Task> tasks) {
        List<Task> sortedTasks = new ArrayList<>(tasks);

        sortedTasks.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                String project1 = getProjectName(t1);
                String project2 = getProjectName(t2);

                if (project1 == null && project2 == null) {
                    return compareTaskTitle(t1, t2);
                }
                if (project1 == null) {
                    return 1;
                }
                if (project2 == null) {
                    return -1;
                }

                int projectComparison = project1.compareToIgnoreCase(project2);
                if (projectComparison != 0) {
                    return projectComparison;
                }

                return compareTaskTitle(t1, t2);
            }
        });

        return sortedTasks;
    }

    private String getProjectName(Task task) {
        if (task == null || task.getProject() == null || task.getProject().getName() == null) {
            return null;
        }

        String name = task.getProject().getName().trim();
        return name.isEmpty() ? null : name;
    }

    private int compareTaskTitle(Task t1, Task t2) {
        String title1 = t1 == null || t1.getTitle() == null ? null : t1.getTitle().trim();
        String title2 = t2 == null || t2.getTitle() == null ? null : t2.getTitle().trim();

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
}