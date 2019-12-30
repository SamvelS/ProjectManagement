package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.Project;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectRepository {
    List<Project> getProjects(int from, int count);

    Project getProjectById(int id);

    void createProject(Project project);

    void editProject(Project project);

    int getProjectsCount();
}
