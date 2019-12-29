package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.repositoriy.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JDBCProjectRepository implements ProjectRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Project> getProjects(int from, int count) {
        return new ArrayList<Project>();
    }

    @Override
    public void createProject(Project project) {
        this.jdbcTemplate.update("insert into project(name, description, created_on, planned_start_date, planned_end_date, status_id)" +
                " values(?,?,now(),?,?,1)", new Object[]{ project.getName(), project.getDescription(), project.getPlannedStartDate(), project.getPlannedEndDate() });
    }
}
