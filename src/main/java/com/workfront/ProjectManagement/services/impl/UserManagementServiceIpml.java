package com.workfront.ProjectManagement.services.impl;

import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.OnClose;
import java.util.List;

@Service
public class UserManagementServiceIpml implements UserManagementService {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getUsers(int from, int count) {
        return this.userRepository.getUsers(from, count);
    }

    @Override
    public int getUsersCount() {
        return this.userRepository.getUsersCount();
    }

    @Override
    public User getUserById(int id) {
        return this.userRepository.getUserById(id);
    }

    @Override
    public void createUser(User user) {
        this.userRepository.createUser(user);
    }

    @Override
    public void editUser(User user) {
        this.userRepository.editUser(user);
    }

    @Override
    public void deleteUserById(int id) {
        this.userRepository.deleteUserById(id);
    }
}
