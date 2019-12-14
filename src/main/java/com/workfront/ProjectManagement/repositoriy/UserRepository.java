package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.User;
import org.springframework.stereotype.Component;

@Component
public interface UserRepository {
    User getUserByUsername(String username);
}
