package com.workfront.ProjectManagement.services.impl;

import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import com.workfront.ProjectManagement.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementServiceIpml implements UserManagementService {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getUsers(int from, int count) {
        return this.userRepository.getUsers(from, count);
    }
}