package com.incubyte.payflow.repository;

import com.incubyte.payflow.domain.EmploymentStatusReference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmploymentStatusReferenceRepository extends JpaRepository<EmploymentStatusReference, String> {
    List<EmploymentStatusReference> findAllByActiveTrueOrderBySortOrderAsc();
}
