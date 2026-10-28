package com.incubyte.payflow.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "salary_audit",
    indexes = {
        @Index(name = "idx_salary_audit_employee", columnList = "employee_id"),
        @Index(name = "idx_salary_audit_action_at", columnList = "action_at")
    }
)
public class SalaryAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_audit_id")
    private Long salaryAuditId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false, foreignKey = @ForeignKey(name = "fk_salary_audit_employee"))
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_salary_record_id", foreignKey = @ForeignKey(name = "fk_salary_audit_old_record"))
    private SalaryRecord oldSalaryRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_salary_record_id", foreignKey = @ForeignKey(name = "fk_salary_audit_new_record"))
    private SalaryRecord newSalaryRecord;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private SalaryActionType actionType;

    @Column(name = "actor_id", nullable = false, length = 100)
    private String actorId;

    @Column(name = "action_at", nullable = false)
    private LocalDateTime actionAt;

    @Column(name = "reason", nullable = false, length = 250)
    private String reason;

    protected SalaryAudit() {
    }

    public SalaryAudit(Employee employee, SalaryRecord oldSalaryRecord, SalaryRecord newSalaryRecord,
                      SalaryActionType actionType, String actorId, LocalDateTime actionAt, String reason) {
        this.employee = employee;
        this.oldSalaryRecord = oldSalaryRecord;
        this.newSalaryRecord = newSalaryRecord;
        this.actionType = actionType;
        this.actorId = actorId;
        this.actionAt = actionAt;
        this.reason = reason;
    }

    public Long getSalaryAuditId() {
        return salaryAuditId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public SalaryRecord getOldSalaryRecord() {
        return oldSalaryRecord;
    }

    public void setOldSalaryRecord(SalaryRecord oldSalaryRecord) {
        this.oldSalaryRecord = oldSalaryRecord;
    }

    public SalaryRecord getNewSalaryRecord() {
        return newSalaryRecord;
    }

    public void setNewSalaryRecord(SalaryRecord newSalaryRecord) {
        this.newSalaryRecord = newSalaryRecord;
    }

    public SalaryActionType getActionType() {
        return actionType;
    }

    public void setActionType(SalaryActionType actionType) {
        this.actionType = actionType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public LocalDateTime getActionAt() {
        return actionAt;
    }

    public void setActionAt(LocalDateTime actionAt) {
        this.actionAt = actionAt;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
