package com.taskmanagement.repository;

import com.taskmanagement.domain.Tag;

import java.util.List;

/**
 * Repository interface for tag operations.
 */
public interface TagRepository {
    void addTag(Tag tag);

    Tag findByName(String name);

    List<Tag> findAll();
}
