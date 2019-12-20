package com.workfront.ProjectManagement.web;

import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.services.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping
    public String getManageUsersView(/*@RequestParam(value = "from", defaultValue = "1") int from,
                                     @RequestParam(value = "count", defaultValue = "10") int count, Model model*/) {
        //model.addAttribute("users", this.userManagementService.getUsers(from - 1, count));
        //model.addAttribute("usersCount", this.userManagementService.getUsersCount());

        return "manageUsers";
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

    @GetMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUserView() {
        return "addUser";
    }

    @PostMapping("/add")
    public String processAddUser() {
        return "";
    }
}
