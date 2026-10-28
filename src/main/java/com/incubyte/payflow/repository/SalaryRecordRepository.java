package com.incubyte.payflow.repository;

import com.incubyte.payflow.domain.SalaryRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRecordRepository extends JpaRepository<SalaryRecord, Long> {
    List<SalaryRecord> findByEmployeeEmployeeIdOrderByEffectiveFromDesc(Long employeeId);
}
