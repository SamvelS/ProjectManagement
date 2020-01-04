package com.workfront.ProjectManagement.domain;

public enum UserStatus {
    CHANGE_PASSWORD(1),
    ACTIVE_USER(2);

    private final int userStatusId;

    private UserStatus(int userStatusId) {
        this.userStatusId = userStatusId;
    }

    public int getValue() {
        return this.userStatusId;
    }
}
