package com.taskmanagement.command;

import com.taskmanagement.domain.Tag;
import com.taskmanagement.repository.TagCatalog;
import com.taskmanagement.repository.TagRepository;

/**
 * Command to create a tag (category).
 */
public class CreateTagCommand implements Command {
    private final TagRepository tagRepository;
    private final String tagName;

    public CreateTagCommand(String tagName) {
        this(TagCatalog.getInstance(), tagName);
    }

    public CreateTagCommand(TagRepository tagRepository, String tagName) {
        this.tagRepository = tagRepository;
        this.tagName = tagName;
    }

    @Override
    public void execute() {
        if (tagRepository == null) {
            throw new IllegalStateException("Tag repository cannot be null");
        }
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }

        Tag tag = new Tag(tagName.trim());
        tagRepository.addTag(tag);
        System.out.println("Tag created: " + tag.getName());
    }
}
