package com.workfront.ProjectManagement.repositoriy.impl.jdbc;

import com.workfront.ProjectManagement.domain.Permission;
import com.workfront.ProjectManagement.repositoriy.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JDBCPermissionRepository implements PermissionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Permission> getPermissionsForRole(Integer id) {

        return this.mapPermissions(this.jdbcTemplate.queryForList("select * from permission p" +
                " left join role_permission rp on p.id=rp.permission_id" +
                " where rp.role_id=?", new Object[]{ id }));
    }

    private List<Permission> mapPermissions(List<Map<String, Object>> rows) {
        List<Permission> rolePermissions = new ArrayList<>();

        for(Map row : rows) {
            Permission permission = new Permission();
            permission.setId((int)row.get("id"));
            permission.setName((String) row.get("name"));
            permission.setDescription((String)row.get("description"));

            rolePermissions.add(permission);
        }

        return rolePermissions;
    }
}
