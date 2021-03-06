package com.workfront.ProjectManagement.services.impl;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import com.workfront.ProjectManagement.repositoriy.ProjectRepository;
import com.workfront.ProjectManagement.repositoriy.TaskRepository;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.services.TaskManagementService;
import com.workfront.ProjectManagement.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskManagementServiceImpl implements TaskManagementService {
    @Autowired
    private ActionStatusRepository actionStatusRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<ActionStatus> getActionStatuses() {
        List<ActionStatus> statuses = this.actionStatusRepository.getActionStatuses();
        ActionStatus allStatuses = new ActionStatus();
        allStatuses.setId(Constants.getAllStatusesId());
        allStatuses.setName(Constants.getAllStatusesName());

        statuses.add(0, allStatuses);
        return statuses;
    }

    @Override
    public List<Project> getProjectsByActionStatus(int actionStatusId) {
        return this.projectRepository.getProjectsByStatusId(actionStatusId);
    }

    @Override
    public List<User> getAllUsers() {
        return this.userRepository.getAllUsers();
    }

    @Override
    public List<Task> getTasksInfo(int from, int count, int projectId, int userId) {
        return this.taskRepository.getTasksInfo(from, count, projectId, userId);
    }

    @Override
    public List<Task> getAllTasksInfo(int projectId, int userId) {
        return this.taskRepository.getAllTasksInfo(projectId, userId);
    }

    @Override
    public Task getTaskDetails(int id) {
        return this.taskRepository.getTaskDetails(id);
    }

    @Override
    public int getTasksCount(int projectId, int userId) {
        return this.taskRepository.getTasksCount(projectId, userId);
    }

    @Override
    public void createTask(Task task) {
        this.taskRepository.createTask(task);
    }

    @Override
    public void editTask(Task task) {
        this.taskRepository.editTask(task);
    }

    @Override
    public void deleteTaskById(int id) {
        this.taskRepository.deleteTaskById(id);
    }
}
