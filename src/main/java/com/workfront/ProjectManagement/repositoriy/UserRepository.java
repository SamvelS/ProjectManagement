package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserRepository {
    User getUserByEmail(String email);

    User getUserById(int id);

    List<User> getUsers(int from, int count);

    void createUser(User user);

    void editUser(User user);

    void deleteUserById(int id);

    int getUsersCount();
}
