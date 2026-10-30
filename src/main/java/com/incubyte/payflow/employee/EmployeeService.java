package com.incubyte.payflow.employee;

import com.incubyte.payflow.domain.Country;
import com.incubyte.payflow.domain.Department;
import com.incubyte.payflow.domain.Employee;
import com.incubyte.payflow.domain.EmploymentStatus;
import com.incubyte.payflow.repository.CountryRepository;
import com.incubyte.payflow.repository.DepartmentRepository;
import com.incubyte.payflow.repository.EmployeeRepository;
import com.incubyte.payflow.repository.SalaryAuditRepository;
import com.incubyte.payflow.repository.SalaryRecordRepository;
import com.incubyte.payflow.salary.SalaryChangeRequest;
import com.incubyte.payflow.salary.SalaryHistoryItem;
import com.incubyte.payflow.salary.SalaryService;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final SalaryAuditRepository salaryAuditRepository;
    private final SalaryService salaryService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           CountryRepository countryRepository,
                           DepartmentRepository departmentRepository,
                           SalaryRecordRepository salaryRecordRepository,
                           SalaryAuditRepository salaryAuditRepository,
                           SalaryService salaryService) {
        this.employeeRepository = employeeRepository;
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.salaryAuditRepository = salaryAuditRepository;
        this.salaryService = salaryService;
    }

    @Transactional
    public EmployeeOnboardingResponse onboardEmployee(EmployeeOnboardingRequest request) {
        if (request == null || request.employee() == null || request.initialSalary() == null) {
            throw new IllegalArgumentException("Employee details and initial salary are required");
        }

        EmployeeResponse employee = createEmployee(request.employee());
        InitialSalaryRequest initialSalary = request.initialSalary();
        SalaryHistoryItem salary = salaryService.createInitialSalary(new SalaryChangeRequest(
            employee.id(),
            initialSalary.amount(),
            initialSalary.currencyCode(),
            initialSalary.payFrequency(),
            initialSalary.effectiveFrom(),
            null,
            initialSalary.reason()
        ), request.employee().hireDate());
        return new EmployeeOnboardingResponse(employee, salary);
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
        if (request == null) {
            throw new IllegalArgumentException("Employee request is required");
        }

        Employee employee = employeeRepository.findById(request.id())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        validateCreateRequest(new EmployeeCreateRequest(
            employee.getEmployeeNumber(),
            request.firstName(),
            request.lastName(),
            request.email(),
            request.countryCode(),
            request.departmentCode(),
            request.jobTitle(),
            request.employmentStatus(),
            request.hireDate()
        ));

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

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
            .map(this::toResponse)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> listEmployees() {
        return employeeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> listEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void deleteEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found"));

        if (salaryRecordRepository.existsByEmployee_EmployeeId(employeeId)
            || salaryAuditRepository.existsByEmployee_EmployeeId(employeeId)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Employee cannot be deleted because salary history or audit records exist"
            );
        }

        employeeRepository.delete(employee);
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
        if (request.firstName() == null || request.firstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.lastName() == null || request.lastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
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
