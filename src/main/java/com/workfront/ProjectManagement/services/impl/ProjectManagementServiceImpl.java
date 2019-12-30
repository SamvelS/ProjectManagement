package com.workfront.ProjectManagement.services.impl;

import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.repositoriy.ProjectRepository;
import com.workfront.ProjectManagement.services.ProjectManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectManagementServiceImpl implements ProjectManagementService {
    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Project> getProjects(int from, int count) {
        return this.projectRepository.getProjects(from, count);
    }

    @Override
    public Project getProjectById(int id) {
        return this.projectRepository.getProjectById(id);
    }

    @Override
    public void createProject(Project project) {
        this.projectRepository.createProject(project);
    }

    @Override
    public void editProject(Project project) {
        this.projectRepository.editProject(project);
    }

    @Override
    public int getProjectsCount() {
        return this.projectRepository.getProjectsCount();
    }

    @Override
    public void deleteProjectById(int id) {
        this.projectRepository.deleteProjectById(id);
    }
}
