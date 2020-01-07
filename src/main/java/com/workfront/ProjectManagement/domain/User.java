package com.workfront.ProjectManagement.domain;

import com.workfront.ProjectManagement.validationOrder.FirstOrder;
import com.workfront.ProjectManagement.validationOrder.SecondOrder;
import com.workfront.ProjectManagement.validationOrder.ThirdOrder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    @NotBlank(message="First name is required", groups = FirstOrder.class)
    @Size(min = 2, max = 50, message = "Fist name length should be between 2 and 50", groups = SecondOrder.class)
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message="Last name is required", groups = FirstOrder.class)
    @Size(min = 2, max = 100, message = "Last name length should be between 2 and 100", groups = SecondOrder.class)
    private String lastName;

    @Column(name = "email")
    @NotBlank(message = "Email is required", groups = FirstOrder.class)
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+[a-zA-Z]", message = "Email is not valid", groups = SecondOrder.class)
    @Size(max = 255, message = "Email length should be max 255 characters", groups = ThirdOrder.class)
    private String email;

    @Column(name = "password")
    @NotBlank(message="Password is required", groups = FirstOrder.class)
    @Size(min = 6, max = 50, message = "Password length should be between 6 and 50", groups = SecondOrder.class)
    private String password;

    @Column(name = "status_id")
    private int statusId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable
            (
                    name = "account_role",
                    joinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id"),
                    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
            )
    private List<Role> roles;

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
        if(this.roles == null) {
            return Collections.emptyList();
        }

        return this.roles.stream().map(r -> r.getPermissions()).collect(Collectors.toList()).stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public int getStatusId() { return this.statusId; }

    public void setStatusId(int statusId) { this.statusId = statusId; }
}
