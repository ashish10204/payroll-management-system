package com.incubyte.payflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_title_reference")
public class JobTitleReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_title_id")
    private Long jobTitleId;

    @Column(name = "job_title_code", nullable = false, length = 50, unique = true)
    private String jobTitleCode;

    @Column(name = "job_title_name", nullable = false, length = 120)
    private String jobTitleName;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    protected JobTitleReference() {
    }

    public JobTitleReference(String jobTitleCode, String jobTitleName, Integer sortOrder, Boolean active) {
        this.jobTitleCode = jobTitleCode;
        this.jobTitleName = jobTitleName;
        this.sortOrder = sortOrder;
        this.active = active;
    }

    public Long getJobTitleId() {
        return jobTitleId;
    }

    public String getJobTitleCode() {
        return jobTitleCode;
    }

    public void setJobTitleCode(String jobTitleCode) {
        this.jobTitleCode = jobTitleCode;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    public void setJobTitleName(String jobTitleName) {
        this.jobTitleName = jobTitleName;
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
