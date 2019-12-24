package com.workfront.ProjectManagement.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@ToString
public class User {
    private int id;
    @NotBlank(message="First name is required")
    @Size(min = 2, max = 50, message = "Fist name length should be between 2 and 50")
    private String firstName;

    @NotBlank(message="Last name is required")
    @Size(min = 2, max = 100, message = "Last name length should be between 2 and 100")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+[a-zA-Z]", message = "Email is not valid")
    @Size(max = 255, message = "Email length should be max 255 characters")
    private String email;

    @NotBlank(message="Password is required")
    @Size(min = 6, max = 50, message = "Password length should be between 6 and 50")
    private String password;

    private List<Role> roles;
    private List<Permission> permissions;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
