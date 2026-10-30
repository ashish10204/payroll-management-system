package com.incubyte.payflow.repository;

import com.incubyte.payflow.domain.JobTitleReference;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobTitleReferenceRepository extends JpaRepository<JobTitleReference, Long> {
    List<JobTitleReference> findAllByActiveTrueOrderBySortOrderAsc();
}
