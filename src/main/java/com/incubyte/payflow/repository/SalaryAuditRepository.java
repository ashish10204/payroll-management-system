package com.incubyte.payflow.repository;

import com.incubyte.payflow.domain.SalaryAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryAuditRepository extends JpaRepository<SalaryAudit, Long> {
    boolean existsByEmployee_EmployeeId(Long employeeId);
}
