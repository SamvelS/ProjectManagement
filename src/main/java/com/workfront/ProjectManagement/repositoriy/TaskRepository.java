package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskRepository {
    List<Task> getTasksInfo(int from, int count, int projectId, int userId);

    int getTasksCount(int projectId, int userId);

    void createTask(Task task);
}
