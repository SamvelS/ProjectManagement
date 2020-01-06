package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.ProjectUserDetails;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import com.workfront.ProjectManagement.repositoriy.TaskRepository;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JDBCTaskRepository implements TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActionStatusRepository actionStatusRepository;

    private ProjectUserDetails userDetails;

    @Override
    public List<Task> getTasksInfo(int from, int count, int projectId, int userId) {
        String query = "select t.id, t.name, t.description, t.parent_task_id from task t";
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

        List<Task> tasks = this.mapTasksInfo(rows);
        List<ActionStatus> actionStatuses = this.actionStatusRepository.getActionStatuses();

        for (Task task :
                tasks) {
            if(task.getParentTask().getId() != null) {
                task.setParentTask(this.getTaskInfo(task.getParentTask().getId()));
            }
            this.setTaskAssigneeAndStatus(task, actionStatuses);
        }

        return tasks;
    }

    @Override
    public List<Task> getAllTasksInfo(int projectId, int userId) {
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
        query += " order by t.name";

        return this.mapTasksInfo(this.jdbcTemplate.queryForList(query, params.toArray()));
    }

    @Override
    public Task getTaskDetails(int id) {
        Task taskDetails = this.jdbcTemplate.queryForObject("select * from task where id=?",
                new Object[]{id}, (rs, i) -> {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setName(rs.getString("name"));
                    task.setDescription(rs.getString("description"));
                    task.setCreatedOn(rs.getTimestamp("created_on"));
                    task.setPlannedStartDate(rs.getTimestamp("planned_start_date"));
                    task.setPlannedEndDate(rs.getTimestamp("planned_end_date"));
                    task.setActualStartDate(rs.getTimestamp("actual_start_date"));
                    task.setActualEndDate(rs.getTimestamp("actual_end_date"));
                    task.setProjectId(rs.getInt("project_id"));
                    User createdBy = new User();
                    createdBy.setId(rs.getInt("created_by"));
                    task.setCreatedBy(createdBy);
                    Task parentTask = new Task();
                    parentTask.setId(rs.getObject("parent_task_id", Integer.class));
                    task.setParentTask(parentTask);

                    return task;
                });

        if(taskDetails != null) {
            taskDetails.setCreatedBy(this.userRepository.getUserById(taskDetails.getCreatedBy().getId()));
            if(taskDetails.getParentTask().getId() != null) {
                taskDetails.setParentTask(this.getTaskInfo(taskDetails.getParentTask().getId()));
            }

            List<ActionStatus> actionStatuses = this.actionStatusRepository.getActionStatuses();

            setTaskAssigneeAndStatus(taskDetails, actionStatuses);
        }

        return taskDetails;
    }

    private void setTaskAssigneeAndStatus(Task task, List<ActionStatus> actionStatuses) {
        List<Map<String, Object>> rows =  this.jdbcTemplate.queryForList("select a.id as account_id, a.first_name, a.last_name, a.email, ta.status_id from task_assignment ta"
                + " left join account a on ta.account_id = a.id where ta.task_id=? order by a.id", new Object[]{ task.getId() });

        task.setAssignees(this.mapUserInfo(rows));

        String status = actionStatuses.stream().filter(s -> s.getId() == Constants.getNotStartedActionStatusId()).findFirst().get().getName();

        if(task.getAssignees() != null && !task.getAssignees().isEmpty()) {
            if (task.getAssignees().stream().anyMatch(a -> a.getStatusId() == Constants.getInProgressActionStatusId())) {
                status = actionStatuses.stream().filter(s -> s.getId() == Constants.getInProgressActionStatusId()).findFirst().get().getName();
            }
            else if(task.getAssignees().stream().allMatch(a -> a.getStatusId() == Constants.getCompletedActionStatusId())) {
                status = actionStatuses.stream().filter(s -> s.getId() == Constants.getCompletedActionStatusId()).findFirst().get().getName();
            }
        }
        task.setStatus(status);
    }

    @Override
    public Task getTaskInfo(int id) {
        Task taskDetails = this.jdbcTemplate.queryForObject("select * from task where id=?",
                new Object[]{id}, (rs, i) -> {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setName(rs.getString("name"));
                    task.setDescription(rs.getString("description"));
                    Task parentTask = new Task();
                    parentTask.setId(rs.getInt("parent_task_id"));

                    return task;
                });

        return taskDetails;
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

    @Transactional
    @Override
    public void createTask(Task task) throws AuthenticationCredentialsNotFoundException {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement statement = con.prepareStatement("insert into task(name, description, created_on, created_by, planned_start_date, planned_end_date, project_id, parent_task_id)"
                        + " values(?,?,now(),?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, task.getName());
                statement.setString(2, task.getDescription());
                statement.setInt(3, getUserDetails().getUserId());
                statement.setDate(4, new java.sql.Date(task.getPlannedStartDate().getTime()));
                statement.setDate(5, new java.sql.Date(task.getPlannedEndDate().getTime()));
                statement.setInt(6, task.getProjectId());
                if (task.getParentTask().getId() == null) {
                    statement.setNull(7, Types.INTEGER);
                } else {
                    statement.setInt(7, task.getParentTask().getId());
                }
                return statement;
            }
        }, holder);

        int primaryKey;
        if (holder.getKeys().size() > 1) {
            primaryKey = (int) holder.getKeys().get("id");
        } else {
            primaryKey = holder.getKey().intValue();
        }

        if (!task.getAssignees().isEmpty()) {
            task.getAssignees().forEach(asgn -> {
                this.jdbcTemplate.update("insert into task_assignment(task_id, account_id, status_id) values(?,?,1)",
                        new Object[]{primaryKey, asgn.getId()});
            });
        }
    }
    
    @Transactional
    @Override
    public void editTask(Task task) {
        this.jdbcTemplate.update("update task set name=?, description=?, planned_start_date=?, planned_end_date=?, actual_start_date=?, actual_end_date=?," +
                        " project_id=?, parent_task_id=? where id=?",
                new Object[]{ task.getName(), task.getDescription(), task.getPlannedStartDate(), task.getPlannedEndDate(), task.getActualStartDate(),
                        task.getActualEndDate(), task.getProjectId(), task.getParentTask().getId(), task.getId() });

        this.jdbcTemplate.update("delete from task_assignment where task_id=?", new Object[] { task.getId() });

        if(task.getAssignees() != null && !task.getAssignees().isEmpty()) {
            for (User assignee :
                    task.getAssignees()) {
                this.jdbcTemplate.update("insert into task_assignment(task_id, account_id, status_id)" +
                        " values(?,?,?)", new Object[]{ task.getId(), assignee.getId(), assignee.getStatusId() });
            }
        }
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
            Task parentTask = new Task();
            parentTask.setId((Integer)row.get("parent_task_id"));
            task.setParentTask(parentTask);
            tasks.add(task);
        }

        return tasks;
    }

    private List<User> mapUserInfo(List<Map<String, Object>> rows) {
        List<User> users = new ArrayList<>();

        for(Map row : rows) {
            User user = new User();
            user.setId((int)row.get("account_id"));
            user.setFirstName((String) row.get("first_name"));
            user.setLastName((String)row.get("last_name"));
            user.setEmail((String)row.get("email"));
            user.setStatusId((int)row.get("status_id"));
            users.add(user);
        }

        return users;
    }
}
