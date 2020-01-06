package com.workfront.ProjectManagement.services;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskManagementService {
    List<ActionStatus> getActionStatuses();

    List<Project> getProjectsByActionStatus(int actionStatusId);

    List<User> getAllUsers();

    List<Task> getTasksInfo(int from, int count, int projectId, int userId);

    List<Task> getAllTasksInfo(int projectId, int userId);

    Task getTaskDetails(int id);

    int getTasksCount(int projectId, int userId);

    void createTask(Task task);

    void editTask(Task task);
}
