package com.workfront.ProjectManagement.services;

import com.workfront.ProjectManagement.domain.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectManagementService {
    List<Project> getProjects(int from, int count);

    Project getProjectById(int id);

    void createProject(Project project);

    void editProject(Project project);

    int getProjectsCount();
}
