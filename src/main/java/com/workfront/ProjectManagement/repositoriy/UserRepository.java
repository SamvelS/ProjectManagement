package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserRepository {
    User getUserByUsername(String username);

    List<User> getUsers(int from, int count);
}
