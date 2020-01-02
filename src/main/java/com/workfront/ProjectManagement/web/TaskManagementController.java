package com.workfront.ProjectManagement.web;

import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.services.TaskManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskManagementController {
    @Autowired
    private TaskManagementService taskManagementService;

    @GetMapping
    public String getManagTasksView(Model model) {
        List<ActionStatus> actionStatuses = this.taskManagementService.getActionStatuses();

        model.addAttribute("actionStatuses", actionStatuses);
        List<Project> projects = new ArrayList<>();
        if(!actionStatuses.isEmpty()) {
            projects = this.taskManagementService.getProjectsByActionStatus(actionStatuses.get(0).getId());
        }
        model.addAttribute("projects", projects);

        return "manageTasks";
    }
}
