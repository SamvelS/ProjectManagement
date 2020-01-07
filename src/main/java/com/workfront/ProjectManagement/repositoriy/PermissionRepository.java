package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.Permission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PermissionRepository {
    List<Permission> getPermissionsForRole(Integer id);
}
