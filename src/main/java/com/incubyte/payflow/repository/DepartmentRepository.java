package com.incubyte.payflow.repository;

import com.incubyte.payflow.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
