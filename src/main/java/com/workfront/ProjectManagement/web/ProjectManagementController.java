package com.workfront.ProjectManagement.web;

import com.google.gson.Gson;
import com.sun.tools.javac.util.Pair;
import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.repositoriy.ActionStatusRepository;
import com.workfront.ProjectManagement.services.ProjectManagementService;
import com.workfront.ProjectManagement.validationOrder.OrderedValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/projects")
public class ProjectManagementController {

    @Autowired
    ProjectManagementService projectManagementService;

    @Autowired
    ActionStatusRepository actionStatusRepository;

    @GetMapping
    public String getManageProjectsView() {
        return "manageProjects";
    }

    @GetMapping("/data")
    public ResponseEntity<List<Project>> getManageProjectsData(@RequestParam(value = "from", defaultValue = "1") int from,
                                                               @RequestParam(value = "count", defaultValue = "10") int count) {
        return ResponseEntity.ok(this.projectManagementService.getProjects(from - 1, count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int id) {
        return ResponseEntity.ok(this.projectManagementService.getProjectById(id));
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<ActionStatus>> getActionStatuses() { return ResponseEntity.ok(this.actionStatusRepository.getActionStatuses()); }

    @GetMapping("/count")
    public ResponseEntity<Integer> getProjectsCount() {
        return ResponseEntity.ok(this.projectManagementService.getProjectsCount());
    }

    @PostMapping("/add")
    public ResponseEntity<String> processAddUser(@RequestBody @Validated(OrderedValidation.class) Project project, Errors errors) {
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
            this.projectManagementService.createProject(project);
        }
        catch (DuplicateKeyException ignore) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("name", "Project with name '" + project.getName() + "' already exists"));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("");
    }

    @PostMapping("/edit")
    public ResponseEntity<String> processEditProject(@RequestBody @Validated(OrderedValidation.class) Project project, Errors errors) {
        if(errors.hasErrors()) {
            Map<String, String> fieldErrorsMap = new HashMap<>();
            errors.getFieldErrors().stream().forEach(err -> {
                if(!fieldErrorsMap.containsKey(err.getField())) {
                    fieldErrorsMap.put(err.getField(), err.getDefaultMessage());
                }
            });

            if(!fieldErrorsMap.isEmpty()) {
                List<Pair<String, String>> fieldErrors = new ArrayList<>();
                for (Map.Entry<String, String> err :
                        fieldErrorsMap.entrySet()) {
                    fieldErrors.add(new Pair<>(err.getKey(), err.getValue()));
                }

                return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
            }
        }

        try {
            this.projectManagementService.editProject(project);
        }
        catch (DuplicateKeyException ignore) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("name", "Project with name '" + project.getName() + "' already exists"));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("");
    }
}
