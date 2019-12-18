package com.workfront.ProjectManagement.web;

import com.workfront.ProjectManagement.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping
    public String getManageUsersView(@RequestParam(value = "from", defaultValue = "0") int from,
                                     @RequestParam(value = "count", defaultValue = "10") int count, Model model) {
        model.addAttribute("users", this.userManagementService.getUsers(from, count));
        return "manageUsers";
    }

    @GetMapping("/add")
    public String getUserView() {
        return "addUser";
    }

    @PostMapping("/add")
    public String processAddUser() {
        return "";
    }
}
