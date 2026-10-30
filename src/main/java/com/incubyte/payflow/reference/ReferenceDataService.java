package com.incubyte.payflow.reference;

import com.incubyte.payflow.domain.Country;
import com.incubyte.payflow.domain.Department;
import com.incubyte.payflow.domain.EmploymentStatusReference;
import com.incubyte.payflow.domain.JobTitleReference;
import com.incubyte.payflow.repository.CountryRepository;
import com.incubyte.payflow.repository.DepartmentRepository;
import com.incubyte.payflow.repository.EmploymentStatusReferenceRepository;
import com.incubyte.payflow.repository.JobTitleReferenceRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReferenceDataService {

    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final JobTitleReferenceRepository jobTitleReferenceRepository;
    private final EmploymentStatusReferenceRepository employmentStatusReferenceRepository;

    public ReferenceDataService(CountryRepository countryRepository,
                               DepartmentRepository departmentRepository,
                               JobTitleReferenceRepository jobTitleReferenceRepository,
                               EmploymentStatusReferenceRepository employmentStatusReferenceRepository) {
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleReferenceRepository = jobTitleReferenceRepository;
        this.employmentStatusReferenceRepository = employmentStatusReferenceRepository;
    }

    public ReferenceDataResponse getReferenceData() {
        List<LookupOption> countries = countryRepository.findAll(Sort.by("countryName")).stream()
            .map(this::toCountryOption)
            .toList();

        List<LookupOption> departments = departmentRepository.findAll(Sort.by("departmentName")).stream()
            .map(this::toDepartmentOption)
            .toList();

        List<LookupOption> jobTitles = jobTitleReferenceRepository.findAllByActiveTrueOrderBySortOrderAsc().stream()
            .map(this::toJobTitleOption)
            .toList();

        List<LookupOption> employmentStatuses = employmentStatusReferenceRepository.findAllByActiveTrueOrderBySortOrderAsc().stream()
            .map(this::toEmploymentStatusOption)
            .toList();

        return new ReferenceDataResponse(countries, departments, jobTitles, employmentStatuses);
    }

    private LookupOption toCountryOption(Country country) {
        return new LookupOption(country.getCountryCode(), country.getCountryName());
    }

    private LookupOption toDepartmentOption(Department department) {
        return new LookupOption(department.getDepartmentCode(), department.getDepartmentName());
    }

    private LookupOption toJobTitleOption(JobTitleReference jobTitleReference) {
        return new LookupOption(jobTitleReference.getJobTitleCode(), jobTitleReference.getJobTitleName());
    }

    private LookupOption toEmploymentStatusOption(EmploymentStatusReference employmentStatusReference) {
        return new LookupOption(employmentStatusReference.getStatusCode(), employmentStatusReference.getStatusName());
    }
}
