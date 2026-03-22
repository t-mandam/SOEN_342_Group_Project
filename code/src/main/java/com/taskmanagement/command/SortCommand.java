package com.taskmanagement.command;

import com.taskmanagement.domain.Task;
import com.taskmanagement.strategy.SortStrategy;
import com.taskmanagement.repository.TaskRepository;

import java.util.List;

public class SortCommand implements Command {
    private SortStrategy strategy;
    private TaskRepository taskRepository;
    private List<Task> sortedTasks;

    public SortCommand() {}

    public void setStrategy(SortStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Task> getSortedTasks(List<Task> tasks) {
        return sortedTasks;
    }

    @Override
    public void execute() {
        List<Task> tasks = taskRepository.findAll();
        this.sortedTasks = strategy.sort(tasks);
        // The sorted tasks would typically be displayed or returned to the caller
        // For this skeleton, we're just executing the sort operation
    }
}