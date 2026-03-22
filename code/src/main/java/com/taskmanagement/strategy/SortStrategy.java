package com.taskmanagement.strategy;

import com.taskmanagement.domain.Task;

import java.util.List;

public interface SortStrategy {

    List<Task> sort(List<Task> tasks);
}