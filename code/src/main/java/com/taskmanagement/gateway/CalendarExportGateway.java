package com.taskmanagement.gateway;

import com.taskmanagement.domain.Task;
import java.io.IOException;
import java.util.List;

public interface CalendarExportGateway {
    void exportTasks(List<Task> tasks, String filePath) throws IOException;
}
