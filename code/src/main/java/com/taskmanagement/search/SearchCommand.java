package com.taskmanagement.search;

import com.taskmanagement.command.Command;
import com.taskmanagement.domain.Task;
import com.taskmanagement.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Command to search for tasks using various criteria
 */
public class SearchCommand implements Command {
    private TaskRepository taskRepository;
    private List<SearchCriterion> criteria;
    private List<Task> searchResults;

    public SearchCommand() {
        this.criteria = new ArrayList<>();
        this.searchResults = new ArrayList<>();
    }

    public SearchCommand(TaskRepository taskRepository) {
        this();
        this.taskRepository = taskRepository;
    }

    /**
     * Searches for tasks that match all the given criteria
     * @param repo the repository to search in
     * @param criteria the search criteria to apply
     * @return list of matching tasks
     */
    public List<Task> search(TaskRepository repo, List<SearchCriterion> criteria) {
        List<Task> allTasks = repo.findAll();
        List<Task> matchingTasks = new ArrayList<>();

        for (Task task : allTasks) {
            boolean matchesAllCriteria = true;

            // Task must match ALL criteria
            for (SearchCriterion criterion : criteria) {
                if (!criterion.matches(task)) {
                    matchesAllCriteria = false;
                    break;
                }
            }

            if (matchesAllCriteria) {
                matchingTasks.add(task);
            }
        }

        return matchingTasks;
    }

    @Override
    public void execute() {
        if (taskRepository == null) {
            throw new IllegalStateException("Task repository cannot be null");
        }

        searchResults = search(taskRepository, criteria);
    }

    /**
     * Adds a search criterion
     * @param criterion the criterion to add
     */
    public void addCriterion(SearchCriterion criterion) {
        if (criterion != null) {
            criteria.add(criterion);
        }
    }

    /**
     * Removes a search criterion
     * @param criterion the criterion to remove
     */
    public void removeCriterion(SearchCriterion criterion) {
        criteria.remove(criterion);
    }

    /**
     * Clears all search criteria
     */
    public void clearCriteria() {
        criteria.clear();
    }

    // Getters and setters
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<SearchCriterion> getCriteria() {
        return new ArrayList<>(criteria);
    }

    public void setCriteria(List<SearchCriterion> criteria) {
        this.criteria = criteria != null ? new ArrayList<>(criteria) : new ArrayList<>();
    }

    public List<Task> getSearchResults() {
        return new ArrayList<>(searchResults);
    }
}