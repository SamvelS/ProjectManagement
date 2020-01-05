package com.workfront.ProjectManagement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workfront.ProjectManagement.constraint.DateRangeMatch;
import com.workfront.ProjectManagement.constraint.NullableDateRangeMatch;
import com.workfront.ProjectManagement.validationOrder.FirstOrder;
import com.workfront.ProjectManagement.validationOrder.SecondOrder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@DateRangeMatch(startDate = "plannedStartDate", endDate = "plannedEndDate", message = "Start Date should be before or equal to End Date", groups = FirstOrder.class)
@NullableDateRangeMatch(startDate = "actualStartDate", endDate = "actualEndDate", message = "Start Date should be before or equal to End Date", groups = FirstOrder.class)
public class Task {
    private Integer id;

    @NotBlank(message = "Name is required", groups = FirstOrder.class)
    @Size(min = 2, max = 100, message = "Name length should be between 2 and 100", groups = SecondOrder.class)
    private String name;

    @NotBlank(message = "Description is required", groups = FirstOrder.class)
    @Size(min = 2, max = 255, message = "Description length should be between 2 and 255", groups = SecondOrder.class)
    private String description;

    @NotNull(message = "Start Date is required", groups = FirstOrder.class)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date plannedStartDate;

    @NotNull(message = "End Date is required", groups = FirstOrder.class)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date plannedEndDate;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date actualStartDate;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date actualEndDate;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date createdOn;

    private String status;

    private int projectId;

    private Task parentTask;

    private User createdBy;

    private List<User> assignees;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Date actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public void setParentTask(Task parentTask) {
        this.parentTask = parentTask;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<User> assignees) {
        this.assignees = assignees;
    }
}
