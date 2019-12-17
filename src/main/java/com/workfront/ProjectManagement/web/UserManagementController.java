package com.workfront.ProjectManagement.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserManagementController {

    @GetMapping("/add")
    public String getUserView() {
        return "addUser";
    }

    @PostMapping("/add")
    public String processAddUser() {
        return "";
    }
}
