package com.workfront.ProjectManagement.domain;

import com.workfront.ProjectManagement.constraint.FieldMatch;
import com.workfront.ProjectManagement.validationOrder.FirstOrder;
import com.workfront.ProjectManagement.validationOrder.SecondOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldMatch(first = "newPassword", second = "confirmPassword", message = "The password fields must match")
public class ChangePassword {

    @NotBlank(message="Old Password is required", groups = FirstOrder.class)
    @Size(min = 6, max = 50, message = "Old Password length should be between 6 and 50", groups = SecondOrder.class)
    private String oldPassword;

    @NotBlank(message="New Password is required", groups = FirstOrder.class)
    @Size(min = 6, max = 50, message = "New Password length should be between 6 and 50", groups = SecondOrder.class)
    private String newPassword;

    @NotBlank(message="Confirm Password is required", groups = FirstOrder.class)
    @Size(min = 6, max = 50, message = "Confirm Password length should be between 6 and 50", groups = SecondOrder.class)

    private String confirmPassword;

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
