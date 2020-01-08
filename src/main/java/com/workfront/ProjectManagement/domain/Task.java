package com.workfront.ProjectManagement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workfront.ProjectManagement.constraint.DateRangeMatch;
import com.workfront.ProjectManagement.constraint.NullableDateRangeMatch;
import com.workfront.ProjectManagement.validationOrder.FirstOrder;
import com.workfront.ProjectManagement.validationOrder.SecondOrder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "task")
@DateRangeMatch(startDate = "plannedStartDate", endDate = "plannedEndDate", message = "Start Date should be before or equal to End Date", groups = FirstOrder.class)
@NullableDateRangeMatch(startDate = "actualStartDate", endDate = "actualEndDate", message = "Start Date should be before or equal to End Date", groups = FirstOrder.class)
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    @NotBlank(message = "Name is required", groups = FirstOrder.class)
    @Size(min = 2, max = 100, message = "Name length should be between 2 and 100", groups = SecondOrder.class)
    private String name;

    @Column(name = "description")
    @NotBlank(message = "Description is required", groups = FirstOrder.class)
    @Size(min = 2, max = 255, message = "Description length should be between 2 and 255", groups = SecondOrder.class)
    private String description;

    @Column(name = "planned_start_date")
    @NotNull(message = "Start Date is required", groups = FirstOrder.class)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date plannedStartDate;

    @Column(name = "planned_end_date")
    @NotNull(message = "End Date is required", groups = FirstOrder.class)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date plannedEndDate;

    @Column(name = "actual_start_date")
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date actualStartDate;

    @Column(name = "actual_end_date")
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date actualEndDate;

    @JsonFormat(pattern = "MM/dd/yyyy")
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @Transient
    private String status;

    @Column(name = "project_id")
    private int projectId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable
            (
                    name = "task_assignment",
                    joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
                    inverseJoinColumns = @JoinColumn(name = "account_id", referencedColumnName = "id")
            )
    @Fetch(FetchMode.SELECT)
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
