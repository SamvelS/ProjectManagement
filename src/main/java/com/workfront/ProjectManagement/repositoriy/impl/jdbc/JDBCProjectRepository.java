package com.workfront.ProjectManagement.repositoriy.impl.jdbc;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.repositoriy.ProjectRepository;
import com.workfront.ProjectManagement.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(name = "dbType", havingValue = "jdbc", matchIfMissing = true)
public class JDBCProjectRepository implements ProjectRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Project> getProjects(int from, int count) {
        List<Map<String, Object>> rows  = this.jdbcTemplate.queryForList("select prj.id, prj.name, prj.description, prj.planned_start_date,"
                + " prj.planned_end_date, prj.actual_start_date, prj.actual_end_date, prj.status_id, acts.name as status from project prj"
                        + " left join action_status acts on acts.id = prj.status_id"
                        + " order by id limit ? offset ?",
                new Object[] { count, from });

        return this.mapProjects(rows);
    }

    @Override
    public List<Project> getProjectsByStatusId(int statusId) {
        String query = "select prj.id, prj.name, prj.description, prj.planned_start_date,"
                + " prj.planned_end_date, prj.actual_start_date, prj.actual_end_date, prj.status_id, acts.name as status from project prj"
                + " left join action_status acts on acts.id = prj.status_id";

        if(statusId != Constants.getAllStatusesId()) {
            query += " where prj.status_id=?";
        }

        query += " order by id";

        List<Map<String, Object>> rows  = (statusId != Constants.getAllStatusesId() ?
                this.jdbcTemplate.queryForList(query, new Object[]{ statusId }) :
                this.jdbcTemplate.queryForList(query));

        return this.mapProjects(rows);
    }

    @Override
    public Project getProjectById(int id) {
        return this.jdbcTemplate.queryForObject("select prj.id, prj.name, prj.description, prj.planned_start_date,"
                        + " prj.planned_end_date, prj.actual_start_date, prj.actual_end_date, prj.status_id, acts.name as status from project prj"
                        + " left join action_status acts on acts.id = prj.status_id"
                        + " where prj.id=?",
                new Object[] { id }, (rs, i) -> {
                    Project prj = new Project();
                    prj.setId(rs.getInt("id"));
                    prj.setName(rs.getString("name"));
                    prj.setDescription(rs.getString("description"));
                    prj.setPlannedStartDate(rs.getTimestamp("planned_start_date"));
                    prj.setPlannedEndDate(rs.getTimestamp("planned_end_date"));
                    prj.setActualStartDate(rs.getTimestamp("actual_start_date"));
                    prj.setActualEndDate(rs.getTimestamp("actual_end_date"));

                    ActionStatus status = new ActionStatus();
                    status.setId(rs.getInt("status_id"));
                    status.setName(rs.getString("status"));
                    prj.setStatus(status);

                    return prj;
                });
    }

    @Override
    public void createProject(Project project) {
        this.jdbcTemplate.update("insert into project(name, description, created_on, planned_start_date, planned_end_date, status_id)" +
                " values(?,?,now(),?,?,1)", new Object[]{ project.getName(), project.getDescription(), project.getPlannedStartDate(), project.getPlannedEndDate() });
    }

    @Override
    public int getProjectsCount() {
        return this.jdbcTemplate.queryForObject("select count(id) from project", Integer.class);
    }

    @Override
    public void editProject(Project project) {
        this.jdbcTemplate.update("update project set name=?, description=?, planned_start_date=?, planned_end_date=?, actual_start_date=?, actual_end_date=?," +
                " status_id=? where id=?",
                new Object[]{ project.getName(), project.getDescription(), project.getPlannedStartDate(), project.getPlannedEndDate(), project.getActualStartDate(),
                project.getActualEndDate(), project.getStatus().getId(), project.getId() });
    }

    @Transactional
    @Override
    public void deleteProjectById(int id) {
        this.jdbcTemplate.update("delete from task_assignment where task_id in (select id from task where project_id=?)", new Object[] { id });

        this.jdbcTemplate.update("delete from task where id in (select id from task where project_id=?)",  new Object[] { id });

        this.jdbcTemplate.update("delete from project where id=?", new Object[] { id });
    }

    private List<Project> mapProjects(List<Map<String, Object>> rows) {
        List<Project> projects = new ArrayList<>();

        for(Map row : rows) {
            Project project = new Project();
            project.setId((int)row.get("id"));
            project.setName((String) row.get("name"));
            project.setDescription((String)row.get("description"));
            project.setPlannedStartDate((Date) row.get("planned_start_date"));
            project.setPlannedEndDate((Date) row.get("planned_end_date"));
            project.setActualStartDate((Date) row.get("actual_start_date"));
            project.setActualEndDate((Date) row.get("actual_end_date"));
            ActionStatus status = new ActionStatus();
            status.setId((int)row.get("status_id"));
            status.setName((String) row.get("status"));
            project.setStatus(status);

            projects.add(project);
        }

        return projects;
    }
}
