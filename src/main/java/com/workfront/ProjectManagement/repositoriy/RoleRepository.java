package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoleRepository {
    List<Role> getRoles();
    List<Role> getUserRoles(int userId);
}
