package com.incubyte.payflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employment_status_reference")
public class EmploymentStatusReference {

    @Id
    @Column(name = "status_code", nullable = false, length = 20)
    private String statusCode;

    @Column(name = "status_name", nullable = false, length = 50)
    private String statusName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    protected EmploymentStatusReference() {
    }

    public EmploymentStatusReference(String statusCode, String statusName, Integer sortOrder, Boolean active) {
        this.statusCode = statusCode;
        this.statusName = statusName;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
