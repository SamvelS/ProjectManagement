package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.Role;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.domain.UserStatus;
import com.workfront.ProjectManagement.repositoriy.PermissionRepository;
import com.workfront.ProjectManagement.repositoriy.RoleRepository;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.utilities.Beans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    @Override
    public User getUserByEmail(String email, boolean getPassword) {
        User requestedUser =  this.jdbcTemplate.queryForObject("select * from account where email=?",
                new Object[] { email },
                (rs, i) -> this.createUserFromResultSet(rs, i, getPassword));

        this.initializeUserRolesAndPermissions(requestedUser);

        return requestedUser;
    }

    @Override
    public User getUserById(int id) {
        User requestedUser =  this.jdbcTemplate.queryForObject("select * from account where id=?",
                new Object[] { id },
                (rs, i) -> this.createUserFromResultSet(rs, i, false));

        this.initializeUserRolesAndPermissions(requestedUser);

        return requestedUser;
    }

    @Override
    public List<User> getUsers(int from, int count) {
        List<Map<String, Object>> usersToMap  = this.jdbcTemplate.queryForList("select * from account" +
                " order by id limit ? offset ?"
                , new Object[] { count, from });

        return this.mapUsers(usersToMap);
    }

    @Override
    public List<User> getAllUsers() {
        List<Map<String, Object>> usersToMap  = this.jdbcTemplate.queryForList("select * from account  order by first_name, last_name");

        return this.mapUsers(usersToMap);
    }

    @Override
    @Transactional
    public void createUser(User user) {
        this.jdbcTemplate.update("insert into account(email, password, first_name, last_name, created_on, status_id)" +
                " values(?,?,?,?,now(),1)", new Object[] { user.getEmail(),
                this.beans.passwordEncoder().encode(user.getPassword()),
                user.getFirstName(), user.getLastName()});

        User createdUser = this.getUserByEmail(user.getEmail(), false);

        for (int roleId :
                user.getRoles().stream().map(r -> r.getId()).collect(Collectors.toList())) {
            this.jdbcTemplate.update("insert into account_role(account_id, role_id)" +
                    " values(?,?)", new Object[]{createdUser.getId(), roleId});
        }
    }

    @Override
    @Transactional
    public void editUser(User user) {
        this.jdbcTemplate.update("update account set email=?, first_name=?, last_name=?" +
                " where id=?",
                new Object[] { user.getEmail(), user.getFirstName(), user.getLastName(), user.getId() });

        User createdUser = this.getUserById(user.getId());

        this.jdbcTemplate.update("delete from account_role where account_id=?", new Object[] { user.getId() });

        for (int roleId :
                user.getRoles().stream().map(r -> r.getId()).collect(Collectors.toList())) {
            this.jdbcTemplate.update("insert into account_role(account_id, role_id)" +
                    " values(?,?)", new Object[]{createdUser.getId(), roleId});
        }
    }

    @Override
    @Transactional
    public void deleteUserById(int id) {
        this.jdbcTemplate.update("delete from account_role where account_id=?", new Object[]{ id });

        this.jdbcTemplate.update("delete from account where id=?", new Object[]{ id });

        // TODO : delete from task as well
    }

    @Override
    public int getUsersCount() {
        return this.jdbcTemplate.queryForObject("select count(id) from account", Integer.class);
    }

    @Override
    public void updatePassword(int userId, String newPassword) {
        this.jdbcTemplate.update("update account set password=?, status_id=?" +
                        " where id=?",
                new Object[] { this.beans.passwordEncoder().encode(newPassword), UserStatus.ACTIVE_USER.getValue(), userId });
    }

    private User createUserFromResultSet(ResultSet rs, int i, boolean getPassword) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        if(getPassword) {
            user.setPassword(rs.getString("password"));
        }
        user.setStatusId(rs.getInt("status_id"));
        return user;
    }

    private List<User> mapUsers(List<Map<String, Object>> usersToMap) {
        List<User> users = new ArrayList<>();

        for(Map row : usersToMap) {
            User user = new User();
            user.setId((int)row.get("id"));
            user.setFirstName((String) row.get("first_name"));
            user.setLastName((String) row.get("last_name"));
            user.setEmail((String) row.get("email"));

            this.initializeUserRolesAndPermissions(user);

            users.add(user);
        }

        return users;
    }

    private void initializeUserRolesAndPermissions(User user) {
        List<Role> userRoles = this.roleRepository.getUserRoles(user.getId());

        user.setRoles(userRoles);

        List<Integer> ids = userRoles.stream().map(p -> p.getId()).collect(Collectors.toList());

        user.setPermissions(permissionRepository.getPermissionsForRoles(ids));
    }
}
