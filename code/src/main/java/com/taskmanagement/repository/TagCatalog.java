package com.taskmanagement.repository;

import com.taskmanagement.domain.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory singleton tag repository.
 */
public class TagCatalog implements TagRepository {
    private static final TagCatalog INSTANCE = new TagCatalog();
    private final List<Tag> tagCatalog;

    private TagCatalog() {
        this.tagCatalog = new ArrayList<>();
    }

    public static TagCatalog getInstance() {
        return INSTANCE;
    }

    @Override
    public void addTag(Tag tag) {
        if (tag == null || tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }

        if (findByName(tag.getName()) != null) {
            throw new IllegalArgumentException("Tag already exists: " + tag.getName());
        }

        tagCatalog.add(new Tag(tag.getName().trim()));
    }

    @Override
    public Tag findByName(String name) {
        if (name == null) {
            return null;
        }

        String normalized = name.trim().toLowerCase();
        for (Tag tag : tagCatalog) {
            if (tag.getName() != null && tag.getName().trim().toLowerCase().equals(normalized)) {
                return tag;
            }
        }

        return null;
    }

    @Override
    public List<Tag> findAll() {
        return new ArrayList<>(tagCatalog);
    }
}
