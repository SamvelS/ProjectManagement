package com.workfront.ProjectManagement.web;

import com.google.gson.Gson;
import com.sun.tools.javac.util.Pair;
import com.workfront.ProjectManagement.domain.ActionStatus;
import com.workfront.ProjectManagement.domain.Project;
import com.workfront.ProjectManagement.domain.Task;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.services.TaskManagementService;
import com.workfront.ProjectManagement.utilities.Constants;
import com.workfront.ProjectManagement.validationOrder.OrderedValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public String getManageTasksView(Model model) {
        List<ActionStatus> actionStatuses = this.taskManagementService.getActionStatuses();

        model.addAttribute("actionStatuses", actionStatuses);
        List<Project> projects = new ArrayList<>();
        if(!actionStatuses.isEmpty()) {
            projects = this.taskManagementService.getProjectsByActionStatus(actionStatuses.get(0).getId());
        }
        model.addAttribute("projects", projects);

        List<User> allUsers = new ArrayList<>();
        allUsers.add(this.createAllUsersUser());
        allUsers.addAll(this.taskManagementService.getAllUsers());
        model.addAttribute("users", allUsers);

        return "manageTasks";
    }

    @GetMapping("/data")
    public ResponseEntity<List<Task>>  getManageTasksData(@RequestParam(value = "projectId") int projectId,
                                                          @RequestParam(value = "userId") int userId,
                                                          @RequestParam(value = "from", defaultValue = "1") int from,
                                                          @RequestParam(value = "count", defaultValue = "10") int count) {
        return ResponseEntity.ok(this.taskManagementService.getTasksInfo(from - 1, count, projectId, userId));
    }

    @GetMapping("/allDataInfo")
    public ResponseEntity<List<Task>>  getManageTasksAllData(@RequestParam(value = "projectId") int projectId,
                                                          @RequestParam(value = "userId") int userId) {
        return ResponseEntity.ok(this.taskManagementService.getAllTasksInfo(projectId, userId));
    }

    @GetMapping("/{id}")
    public String getTaskDetailsView(@PathVariable int id, Model model) {
        Task task = this.taskManagementService.getTaskDetails(id);
        model.addAttribute("task", task);
        return "taskDetails";
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Task> getTaskDetails(@PathVariable int id) {
        return ResponseEntity.ok(this.taskManagementService.getTaskDetails(id));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getTasksCount(@RequestParam(value = "projectId") int projectId,
                                                 @RequestParam(value = "userId") int userId) {
        return ResponseEntity.ok(this.taskManagementService.getTasksCount(projectId, userId));
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

    @PostMapping("/edit")
    public ResponseEntity<String> ProcessEditTask(@RequestBody @Validated(OrderedValidation.class) Task task, Errors errors) {
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

        if(task.getParentTask() != null && task.getId() == task.getParentTask().getId()) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("parent-task-area-", "Task can't be its own parent."));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        try {
            this.taskManagementService.editTask(task);
        }
        catch (DuplicateKeyException ignore) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("name", "Task with name '" + task.getName() + "' already exists"));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("");
    }

    private User createAllUsersUser() {
        User allUsersUser = new User();
        allUsersUser.setId(Constants.getAllUsersId());
        allUsersUser.setFirstName(Constants.getAllUsersFirstName());
        allUsersUser.setLastName(Constants.getAllUsersLastName());

        return allUsersUser;
    }
}
