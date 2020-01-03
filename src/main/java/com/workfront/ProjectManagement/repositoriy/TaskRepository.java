package com.workfront.ProjectManagement.repositoriy;

import com.workfront.ProjectManagement.domain.Task;
import org.springframework.stereotype.Component;

@Component
public interface TaskRepository {
    void createTask(Task task);
}
