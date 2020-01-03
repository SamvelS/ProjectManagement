package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.ProjectUserDetails;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.repositoriy.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class JDBCTaskRepository implements TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void createTask(Task task) throws AuthenticationCredentialsNotFoundException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // if user is not authenticated
        if(!(principal instanceof ProjectUserDetails)) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        ProjectUserDetails userDetails = (ProjectUserDetails) principal;

        this.jdbcTemplate.update("insert into task(name, description, created_on, created_by, planned_start_date, planned_end_date, status_id, project_id, parent_task_id)" +
                " values(?,?,now(),?,?,?,1,?,?)",
                new Object[]{ task.getName(), task.getDescription(), userDetails.getUserId(), task.getPlannedStartDate(), task.getPlannedEndDate(), task.getProjectId(), task.getParentTaskId() });
    }
}
