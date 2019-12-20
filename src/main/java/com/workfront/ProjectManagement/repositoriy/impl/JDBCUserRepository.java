package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.Permission;
import com.workfront.ProjectManagement.domain.Role;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JDBCUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getUserByUsername(String username) {
        User requestedUser =  this.jdbcTemplate.queryForObject("select * from account where email=?",
                new Object[] { username },
                (rs, i) -> {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                });

        List<Map<String, Object>> rows  = this.jdbcTemplate.queryForList("select * from Role r" +
                " left join account_role ar on r.id = ar.role_id" +
                " where ar.account_id=?", new Object[] { requestedUser.getId() });

        List<Role> userRoles = new ArrayList<>();

        for(Map row : rows) {
            Role role = new Role();
            role.setId((int)row.get("id"));
            role.setName((String) row.get("name"));
            role.setDescription((String)row.get("description"));
            role.setAdmin((boolean)row.get("is_admin"));

            userRoles.add(role);
        }

        requestedUser.setRoles(userRoles);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        Set<Integer> ids = userRoles.stream().map(p -> p.getId()).collect(Collectors.toSet());
        parameters.addValue("ids", ids);

        NamedParameterJdbcTemplate namedJdbcTemplate =
                new NamedParameterJdbcTemplate(this.jdbcTemplate.getDataSource());

        List<Permission> userPermissions = namedJdbcTemplate.query("select * from permission p" +
                " left join role_permission rp on p.id=rp.role_id" +
                " where p.id in (:ids)", parameters,
                (rs, i) -> {
                    Permission permission = new Permission();
                    permission.setId(rs.getInt("id"));
                    permission.setName(rs.getString("name"));
                    permission.setDescription(rs.getString("description"));
                    return permission;
        });

        requestedUser.setPermissions(userPermissions);

        return requestedUser;
    }

    @Override
    public List<User> getUsers(int from, int count) {
        List<Map<String, Object>> usersToMap  = this.jdbcTemplate.queryForList("select * from account" +
                " order by first_name, last_name limit ? offset ?"
                , new Object[] { count, from });

        List<User> users = new ArrayList<>();

        for(Map row : usersToMap) {
            User user = new User();
            user.setId((int)row.get("id"));
            user.setFirstName((String) row.get("first_name"));
            user.setLastName((String) row.get("last_name"));
            user.setEmail((String) row.get("email"));

            users.add(user);
        }
        return users;
    }

    @Override
    public int getUsersCount() {
        return this.jdbcTemplate.queryForObject("select count(id) from account", Integer.class);
    }
}
