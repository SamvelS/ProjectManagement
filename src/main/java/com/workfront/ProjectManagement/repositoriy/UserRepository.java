package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserRepository {
    User getUserByEmail(String email, boolean getPassword);

    User getUserById(int id, boolean getPassword);

    List<User> getUsers(int from, int count);

    List<User> getAllUsers();

    void createUser(User user);

    void editUser(User user);

    void deleteUserById(int id);

    void updatePassword(int userId, String newPassword);

    int getUsersCount();
}
