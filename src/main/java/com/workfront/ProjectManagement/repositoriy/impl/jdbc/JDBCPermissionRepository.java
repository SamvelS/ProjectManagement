package com.workfront.ProjectManagement.repositoriy.impl.jdbc;

import com.workfront.ProjectManagement.domain.Permission;
import com.workfront.ProjectManagement.repositoriy.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
//@ConditionalOnProperty(name = "dbType", havingValue = "jdbc", matchIfMissing = true)
public class JDBCPermissionRepository implements PermissionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Permission> getPermissionsForRoles(List<Integer> ids) {
        if(ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);

        NamedParameterJdbcTemplate namedJdbcTemplate =
                new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());

        List<Permission> userPermissions = namedJdbcTemplate.query("select * from permission p" +
                        " left join role_permission rp on p.id=rp.permission_id" +
                        " where rp.role_id in (:ids)", parameters,
                (rs, i) -> {
                    Permission permission = new Permission();
                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));
                    return permission;
                });

        return userPermissions;
    }
}
