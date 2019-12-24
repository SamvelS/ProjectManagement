package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.Role;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.PermissionRepository;
import com.workfront.ProjectManagement.repositoriy.RoleRepository;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.utilities.Beans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JDBCUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private Beans beans;

    public User getUserByEmail(String email) {
        User requestedUser =  this.jdbcTemplate.queryForObject("select * from account where email=?",
                new Object[] { email },
                (rs, i) -> {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                });

        List<Role> userRoles = this.roleRepository.getUserRoles(requestedUser.getId());

        requestedUser.setRoles(userRoles);

        List<Integer> ids = userRoles.stream().map(p -> p.getId()).collect(Collectors.toList());

        requestedUser.setPermissions(permissionRepository.getPermissionsForRoles(ids));

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
    @Transactional
    public void createUser(User user) {
        try {
            this.jdbcTemplate.update("insert into account(email, password, first_name, last_name, created_on, status_id)" +
                    " values(?,?,?,?,now(),1)", new Object[]{user.getEmail(),
                    this.beans.passwordEncoder().encode(user.getPassword()),
                    user.getFirstName(), user.getLastName()});

            User createdUser = this.getUserByEmail(user.getEmail());

            for (int roleId :
                    user.getRoles().stream().map(r -> r.getId()).collect(Collectors.toList())) {
                this.jdbcTemplate.update("insert into account_role(account_id, role_id)" +
                        " values(?,?)", new Object[] {createdUser.getId(), roleId});
            }
        }
        catch (Exception constraintEx) {
            System.out.println(constraintEx.getMessage());
        }
    }

    @Override
    public int getUsersCount() {
        return this.jdbcTemplate.queryForObject("select count(id) from account", Integer.class);
    }
}
