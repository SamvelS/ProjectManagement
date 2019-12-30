package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JDBCActionStatusRepository implements ActionStatusRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ActionStatus> getActionStatuses() {
        List<Map<String, Object>> statusesToMap  = this.jdbcTemplate.queryForList("select * from action_status order by id");
        List<ActionStatus> statuses = new ArrayList<>();

        for(Map row : statusesToMap) {
            ActionStatus status = new ActionStatus();
            status.setId((int)row.get("id"));
            status.setName((String)row.get("name"));

            statuses.add(status);
        }
        return statuses;
    }
}
