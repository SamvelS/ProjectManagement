package com.workfront.ProjectManagement.repositoriy.impl;

import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class JDBCUserRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User getUserByUsername(String username) {
        return  this.jdbcTemplate.queryForObject("select * from account where email=?", new Object[] { username },
                (rs, i) -> {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    return user;
                });
    }
}
