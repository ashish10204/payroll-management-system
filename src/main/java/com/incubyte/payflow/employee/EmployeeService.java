package com.incubyte.payflow.employee;

import com.incubyte.payflow.domain.Country;
import com.incubyte.payflow.domain.Department;
import com.incubyte.payflow.domain.Employee;
import com.incubyte.payflow.domain.EmploymentStatus;
import com.incubyte.payflow.repository.CountryRepository;
import com.incubyte.payflow.repository.DepartmentRepository;
import com.incubyte.payflow.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           CountryRepository countryRepository,
                           DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        validateCreateRequest(request);

        String normalizedEmail = normalizeEmail(request.email());
        if (employeeRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Employee email already exists");
        }
        if (employeeRepository.existsByEmployeeNumber(request.employeeNumber().trim())) {
            throw new IllegalArgumentException("Employee number already exists");
        }

        Country country = countryRepository.findById(request.countryCode().trim().toUpperCase(Locale.ROOT))
            .orElseGet(() -> countryRepository.save(new Country(
                request.countryCode().trim().toUpperCase(Locale.ROOT),
                request.countryCode().trim().toUpperCase(Locale.ROOT),
                "USD"
            )));

        Department department = departmentRepository.findByDepartmentCodeIgnoreCase(request.departmentCode().trim())
            .orElseGet(() -> departmentRepository.save(new Department(
                request.departmentCode().trim(),
                request.departmentCode().trim()
            )));

        Employee employee = new Employee(
            request.employeeNumber().trim(),
            request.firstName().trim(),
            request.lastName().trim(),
            normalizedEmail,
            country,
            department,
            request.jobTitle().trim(),
            EmploymentStatus.valueOf(request.employmentStatus().trim().toUpperCase(Locale.ROOT)),
            request.hireDate()
        );

        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Transactional
    public EmployeeResponse updateEmployee(EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(request.id())
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        String normalizedEmail = normalizeEmail(request.email());
        if (!Objects.equals(employee.getEmail(), normalizedEmail)
            && employeeRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Employee email already exists");
        }

        Country country = countryRepository.findById(request.countryCode().trim().toUpperCase(Locale.ROOT))
            .orElseGet(() -> countryRepository.save(new Country(
                request.countryCode().trim().toUpperCase(Locale.ROOT),
                request.countryCode().trim().toUpperCase(Locale.ROOT),
                "USD"
            )));

        Department department = departmentRepository.findByDepartmentCodeIgnoreCase(request.departmentCode().trim())
            .orElseGet(() -> departmentRepository.save(new Department(
                request.departmentCode().trim(),
                request.departmentCode().trim()
            )));

        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(normalizedEmail);
        employee.setCountry(country);
        employee.setDepartment(department);
        employee.setJobTitle(request.jobTitle().trim());
        employee.setEmploymentStatus(EmploymentStatus.valueOf(request.employmentStatus().trim().toUpperCase(Locale.ROOT)));
        employee.setHireDate(request.hireDate());

        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
            .map(this::toResponse)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
    }

    public List<EmployeeResponse> listEmployees() {
        return employeeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void clearTestData() {
        employeeRepository.deleteAll();
    }

    private void validateCreateRequest(EmployeeCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Employee request is required");
        }
        if (request.employeeNumber() == null || request.employeeNumber().isBlank()) {
            throw new IllegalArgumentException("Employee number is required");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new IllegalArgumentException("Employee email is required");
        }
        if (request.countryCode() == null || request.countryCode().isBlank()) {
            throw new IllegalArgumentException("Country code is required");
        }
        if (request.departmentCode() == null || request.departmentCode().isBlank()) {
            throw new IllegalArgumentException("Department code is required");
        }
        if (request.jobTitle() == null || request.jobTitle().isBlank()) {
            throw new IllegalArgumentException("Job title is required");
        }
        if (request.hireDate() == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
        if (request.employmentStatus() == null || request.employmentStatus().isBlank()) {
            throw new IllegalArgumentException("Employment status is required");
        }
        try {
            EmploymentStatus.valueOf(request.employmentStatus().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported employment status");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
            employee.getEmployeeId(),
            employee.getEmployeeNumber(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail(),
            employee.getCountry() == null ? null : employee.getCountry().getCountryCode(),
            employee.getDepartment() == null ? null : employee.getDepartment().getDepartmentCode(),
            employee.getJobTitle(),
            employee.getEmploymentStatus() == null ? null : employee.getEmploymentStatus().name(),
            employee.getHireDate()
        );
    }
}
