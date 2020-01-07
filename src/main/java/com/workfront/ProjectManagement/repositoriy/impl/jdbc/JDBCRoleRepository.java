package com.workfront.ProjectManagement.repositoriy.impl.jdbc;

import com.workfront.ProjectManagement.domain.Role;
import com.workfront.ProjectManagement.repositoriy.PermissionRepository;
import com.workfront.ProjectManagement.repositoriy.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JDBCRoleRepository implements RoleRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<Role> getRoles() {
        List<Role> roles  = this.mapRoles(this.jdbcTemplate.queryForList("select * from Role r"));

        this.setPermissions(roles);

        return roles;
    }

    @Override
    public List<Role> getUserRoles(int userId) {
        List<Role> roles = this.mapRoles(this.jdbcTemplate.queryForList("select * from Role r" +
                " left join account_role ar on r.id = ar.role_id" +
                " where ar.account_id=?", new Object[] { userId }));

        this.setPermissions(roles);

        return roles;
    }

    private List<Role> mapRoles(List<Map<String, Object>> rows) {
        List<Role> userRoles = new ArrayList<>();

        for(Map row : rows) {
            Role role = new Role();
            role.setId((int)row.get("id"));
            role.setName((String) row.get("name"));
            role.setDescription((String)row.get("description"));

            userRoles.add(role);
        }

        return userRoles;
    }

    private void setPermissions(List<Role> roles) {
        for (Role role :
                roles) {
            role.setPermissions(this.permissionRepository.getPermissionsForRole(role.getId()));
        }
    }
}
