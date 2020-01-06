package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.Task;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TaskRepository {
    List<Task> getTasksInfo(int from, int count, int projectId, int userId);

    List<Task> getAllTasksInfo(int projectId, int userId);

    Task getTaskDetails(int id);

    Task getTaskInfo(int id);

    int getTasksCount(int projectId, int userId);

    void createTask(Task task);

    void editTask(Task task);
}
