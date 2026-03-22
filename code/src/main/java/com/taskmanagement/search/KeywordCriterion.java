package com.taskmanagement.search;

import com.taskmanagement.domain.Task;
import com.taskmanagement.domain.Tag;

/**
 * Search criterion that matches tasks containing a specific keyword in title, description, or tags
 */
public class KeywordCriterion implements SearchCriterion {
    private String keyword;

    public KeywordCriterion() {}

    public KeywordCriterion(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null || keyword == null || keyword.trim().isEmpty()) {
            return false;
        }

        String searchKeyword = keyword.toLowerCase().trim();

        // Check title
        if (task.getTitle() != null && task.getTitle().toLowerCase().contains(searchKeyword)) {
            return true;
        }

        // Check description
        if (task.getDescription() != null && task.getDescription().toLowerCase().contains(searchKeyword)) {
            return true;
        }

        // Check tags
        if (task.getTags() != null) {
            for (Tag tag : task.getTags()) {
                if (tag != null && tag.getName() != null && tag.getName().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
            }
        }

        return false;
    }

    // Getters and setters
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}