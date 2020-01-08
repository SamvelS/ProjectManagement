package com.workfront.ProjectManagement.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.workfront.ProjectManagement.constraint.DateRangeMatch;
import com.workfront.ProjectManagement.constraint.NullableDateRangeMatch;
import com.workfront.ProjectManagement.validationOrder.FirstOrder;
import com.workfront.ProjectManagement.validationOrder.SecondOrder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "project")
@DateRangeMatch(startDate = "plannedStartDate", endDate = "plannedEndDate", message = "Start Date should be before or equal to End Date", groups = FirstOrder.class)
@NullableDateRangeMatch(startDate = "actualStartDate", endDate = "actualEndDate", message = "Start Date should be before or equal to End Date", groups = FirstOrder.class)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

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

    @OneToOne(fetch = FetchType.EAGER)
    private ActionStatus status;

    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public Date getPlannedEndDate() {
        return plannedEndDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public Date getActualEndDate() {
        return actualEndDate;
    }

    public ActionStatus getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public void setPlannedEndDate(Date plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public void setActualEndDate(Date anctualEndDate) {
        this.actualEndDate = anctualEndDate;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
}
