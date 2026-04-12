package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.strategy.SortStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortCommand implements Command {
    private SortStrategy strategy;
    private TaskRepository taskRepository;
    private List<Task> sortedTasks;

    public SortCommand() {
        this.sortedTasks = new ArrayList<>();
    }

    public SortCommand(TaskRepository taskRepository, SortStrategy strategy) {
        this();
        this.taskRepository = taskRepository;
        this.strategy = strategy;
    }

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public SortStrategy getStrategy() {
        return strategy;
    }

    public List<Task> getSortedTasks(List<Task> tasks) {
        return getSortedTasks();
    }

    public List<Task> getSortedTasks() {
        return Collections.unmodifiableList(sortedTasks);
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }
        if (strategy == null) {
            throw new IllegalStateException("Sort strategy cannot be null");
        }

        List<Task> tasks = taskRepository.findAll();
        this.sortedTasks = strategy.sort(tasks);
        if (this.sortedTasks == null) {
            this.sortedTasks = new ArrayList<>();
        }
    }
}