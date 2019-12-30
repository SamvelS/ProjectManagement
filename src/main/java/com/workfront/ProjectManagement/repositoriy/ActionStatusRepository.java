package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.ActionStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ActionStatusRepository {
    List<ActionStatus> getActionStatuses();
}
