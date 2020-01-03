package com.workfront.ProjectManagement.web;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.sun.tools.javac.util.Pair;
import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.services.TaskManagementService;
import com.workfront.ProjectManagement.validationOrder.OrderedValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/add")
    public ResponseEntity<String> processAddTask(@RequestBody @Validated(OrderedValidation.class)Task task, Errors errors) {
        if(errors.hasErrors()) {
            Map<String, String> fieldErrorsMap = new HashMap<>();
            errors.getFieldErrors().stream().forEach(err -> {
                if(!fieldErrorsMap.containsKey(err.getField())) {
                    fieldErrorsMap.put(err.getField(), err.getDefaultMessage());
                }
            });

            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            for (Map.Entry<String, String> err:
                    fieldErrorsMap.entrySet()) {
                fieldErrors.add(new Pair<>(err.getKey(), err.getValue()));
            }

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        try {
            this.taskManagementService.createTask(task);
        }
        catch (DuplicateKeyException ignore) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("name", "Task with name '" + task.getName() + "' already exists"));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("");
    }
}
