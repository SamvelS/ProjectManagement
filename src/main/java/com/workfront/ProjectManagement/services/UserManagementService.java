package com.workfront.ProjectManagement.services;

import com.workfront.ProjectManagement.domain.Role;
import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserManagementService {
    List<User> getUsers(int from, int count);

    int getUsersCount();

    User getUserById(int id);

    void createUser(User user);

    void editUser(User user);

    void deleteUserById(int id);

    void updatePassword(int userId, String newPassword);

    List<Role> getRoles();
}
