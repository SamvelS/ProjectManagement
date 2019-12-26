package com.workfront.ProjectManagement.web;

import com.google.gson.Gson;
import com.sun.tools.javac.util.Pair;
import com.workfront.ProjectManagement.validationOrder.OrderedValidation;
import com.workfront.ProjectManagement.domain.Role;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.RoleRepository;
import com.workfront.ProjectManagement.services.UserManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String getManageUsersView() {
        return "manageUsers";
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(this.userManagementService.getUserById(id));
    }

    @GetMapping("/data")
    public ResponseEntity<List<User>> getManageUsersData(@RequestParam(value = "from", defaultValue = "1") int from,
                                                        @RequestParam(value = "count", defaultValue = "10") int count) {
        return ResponseEntity.ok(this.userManagementService.getUsers(from - 1, count));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getUsersCount() {
        return ResponseEntity.ok(this.userManagementService.getUsersCount());
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(this.roleRepository.getRoles());
    }

    @PostMapping("/add")
    public ResponseEntity<String> processAddUser(@RequestBody @Validated(OrderedValidation.class) User user, Errors errors) {
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
            this.userManagementService.createUser(user);
        }
        catch (DuplicateKeyException ignore) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("email", "Email " + user.getEmail() + " already exists"));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("");
    }

    @PostMapping("/edit")
    public ResponseEntity<String> processEditUser(@RequestBody @Validated(OrderedValidation.class) User user, Errors errors) {
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
            this.userManagementService.editUser(user);
        }
        catch (DuplicateKeyException ignore) {
            List<Pair<String, String>> fieldErrors = new ArrayList<>();
            fieldErrors.add(new Pair<>("email", "Email " + user.getEmail() + " already exists"));

            return new ResponseEntity(new Gson().toJson(fieldErrors), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> processDeleteUser(@PathVariable int id) {
        this.userManagementService.deleteUserById(id);

        return ResponseEntity.ok("");
    }
}
