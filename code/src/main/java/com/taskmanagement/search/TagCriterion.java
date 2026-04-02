package com.taskmanagement.search;

import com.taskmanagement.domain.Tag;
import com.taskmanagement.domain.Task;

/**
 * Search criterion that matches tasks by an exact tag name.
 */
public class TagCriterion implements SearchCriterion {
    private String tagName;

    public TagCriterion() {
    }

    public TagCriterion(String tagName) {
        this.tagName = tagName;
    }

    @Override
    public boolean matches(Task task) {
        if (task == null || tagName == null || tagName.trim().isEmpty() || task.getTags() == null) {
            return false;
        }

        String normalizedTagName = tagName.trim().toLowerCase();
        for (Tag tag : task.getTags()) {
            if (tag != null && tag.getName() != null && tag.getName().trim().toLowerCase().equals(normalizedTagName)) {
                return true;
            }
        }

        return false;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
