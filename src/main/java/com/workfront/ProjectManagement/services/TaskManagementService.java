package com.workfront.ProjectManagement.services;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.domain.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TaskManagementService {
    List<ActionStatus> getActionStatuses();

    List<Project> getProjectsByActionStatus(int actionStatusId);

    void createTask(Task task);
}
