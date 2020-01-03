package com.workfront.ProjectManagement.services.impl;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import com.workfront.ProjectManagement.repositoriy.ProjectRepository;
import com.workfront.ProjectManagement.repositoriy.TaskRepository;
import com.workfront.ProjectManagement.services.TaskManagementService;
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

    @Override
    public List<ActionStatus> getActionStatuses() {
        return this.actionStatusRepository.getActionStatuses();
    }

    @Override
    public List<Project> getProjectsByActionStatus(int actionStatusId) {
        return this.projectRepository.getProjectsByStatusId(actionStatusId);
    }

    @Override
    public void createTask(Task task) {
        this.taskRepository.createTask(task);
    }
}
