package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.ProjectUserDetails;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.repositoriy.TaskRepository;
import com.workfront.ProjectManagement.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JDBCTaskRepository implements TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ProjectUserDetails userDetails;

    @Override
    public List<Task> getTasksInfo(int from, int count, int projectId, int userId) {
        String query = "select t.id, t.name, t.description from task t";
        List<Object> params = new ArrayList<>();

        boolean isForSpecificUser = (userId != Constants.getAllUsersId());
        if(isForSpecificUser) {
            query += " left join task_assignment ta on ta.task_id = t.id";
        }
        query += " where t.project_id=?";
        params.add(projectId);
        if(isForSpecificUser) {
            query += " and ta.account_id=?";
            params.add(userId);
        }
        query += " order by t.id limit ? offset ?";

        params.add(count);
        params.add(from);

        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query, params.toArray());

        return this.mapTasksInfo(rows);
    }

    @Override
    public int getTasksCount(int projectId, int userId) {
        String query = "select count(id) from task t";

        boolean isForSpecificUser = (userId != Constants.getAllUsersId());

        if(isForSpecificUser) {
            query += " left join task_assignment ta on ta.task_id = t.id";
        }

        query += " where t.project_id=?";

        List<Object> params = new ArrayList<>();
        params.add(projectId);

        if(isForSpecificUser) {
            query += " and ta.account_id=?";
            params.add(userId);
        }

        return this.jdbcTemplate.queryForObject(query, params.toArray(), Integer.class);
    }

    @Override
    public void createTask(Task task) throws AuthenticationCredentialsNotFoundException {
        this.jdbcTemplate.update("insert into task(name, description, created_on, created_by, planned_start_date, planned_end_date, status_id, project_id, parent_task_id)" +
                " values(?,?,now(),?,?,?,1,?,?)",
                new Object[]{ task.getName(), task.getDescription(), this.userDetails.getUserId(), task.getPlannedStartDate(), task.getPlannedEndDate(), task.getProjectId(), task.getParentTaskId() });
    }

    private ProjectUserDetails getUserDetails() {
        if (this.userDetails != null) {
            return this.userDetails;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // if user is not authenticated
        if(!(principal instanceof ProjectUserDetails)) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        this.userDetails = (ProjectUserDetails) principal;

        return this.userDetails;
    }

    private List<Task> mapTasksInfo(List<Map<String, Object>> rows) {
        List<Task> tasks = new ArrayList<>();

        for(Map row : rows) {
            Task task = new Task();
            task.setId((int)row.get("id"));
            task.setName((String) row.get("name"));
            task.setDescription((String)row.get("description"));

            tasks.add(task);
        }

        return tasks;
    }
}
