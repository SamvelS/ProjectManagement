package com.workfront.ProjectManagement.services;

import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserManagementService {
    List<User> getUsers(int from, int count);
}
