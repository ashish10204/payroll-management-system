package com.incubyte.payflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.incubyte.payflow.employee.EmployeeCreateRequest;
import com.incubyte.payflow.employee.EmployeeOnboardingRequest;
import com.incubyte.payflow.employee.EmployeeService;
import com.incubyte.payflow.employee.EmployeeUpdateRequest;
import com.incubyte.payflow.employee.InitialSalaryRequest;
import com.incubyte.payflow.repository.SalaryAuditRepository;
import com.incubyte.payflow.repository.SalaryRecordRepository;
import com.incubyte.payflow.salary.SalaryChangeRequest;
import com.incubyte.payflow.salary.SalaryService;
import com.incubyte.payflow.seed.SeedService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(Phase2BackendTest.FixedBusinessClockConfig.class)
@Transactional
class Phase2BackendTest {

    @TestConfiguration
    static class FixedBusinessClockConfig {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private SalaryAuditRepository salaryAuditRepository;

    @Autowired
    private SalaryRecordRepository salaryRecordRepository;

    @Autowired
    private SeedService seedService;

    @BeforeEach
    void setUp() {
        salaryService.clearTestData();
        employeeService.clearTestData();
    }

    @Test
    void createEmployee_shouldPersistAndRejectDuplicateEmail() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
            "EMP-1001",
            "Jane",
            "Doe",
            "jane.doe@example.com",
            "IN",
            "Engineering",
            "Senior Engineer",
            "ACTIVE",
            LocalDate.of(2023, 1, 15)
        );

        var created = employeeService.createEmployee(request);
        assertThat(created.employeeNumber()).isEqualTo("EMP-1001");
        assertThat(created.email()).isEqualTo("jane.doe@example.com");

        assertThatThrownBy(() -> employeeService.createEmployee(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateEmployee_shouldPersistChangedValues() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
            "EMP-2001",
            "John",
            "Smith",
            "john.smith@example.com",
            "US",
            "Finance",
            "Analyst",
            "ACTIVE",
            LocalDate.of(2021, 1, 1)
        );

        var created = employeeService.createEmployee(request);
        EmployeeUpdateRequest update = new EmployeeUpdateRequest(
            created.id(),
            "John",
            "Smith",
            "john.smith@newmail.com",
            "US",
            "Finance",
            "Senior Analyst",
            "ON_LEAVE",
            LocalDate.of(2021, 1, 1)
        );

        var updated = employeeService.updateEmployee(update);
        assertThat(updated.jobTitle()).isEqualTo("Senior Analyst");
        assertThat(updated.employmentStatus()).isEqualTo("ON_LEAVE");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void employeeReads_shouldMapLazyAssociationsOutsideTestTransaction() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
            "EMP-2002",
            "Sam",
            "Taylor",
            "sam.taylor@example.com",
            "US",
            "Operations",
            "Coordinator",
            "ACTIVE",
            LocalDate.of(2024, 3, 1)
        );

        var created = employeeService.createEmployee(request);

        assertThat(employeeService.listEmployees())
            .singleElement()
            .satisfies(employee -> {
                assertThat(employee.departmentCode()).isEqualTo("Operations");
                assertThat(employee.countryCode()).isEqualTo("US");
            });
        assertThat(employeeService.getEmployee(created.id()).departmentCode()).isEqualTo("Operations");
    }

    @Test
    void listEmployees_shouldReturnRequestedPageNewestFirst() {
        employeeService.createEmployee(new EmployeeCreateRequest(
            "EMP-PAGE-1", "First", "Employee", "page1@example.com", "US", "Operations",
            "Coordinator", "ACTIVE", LocalDate.of(2024, 1, 1)
        ));
        employeeService.createEmployee(new EmployeeCreateRequest(
            "EMP-PAGE-2", "Second", "Employee", "page2@example.com", "US", "Operations",
            "Coordinator", "ACTIVE", LocalDate.of(2024, 1, 1)
        ));
        employeeService.createEmployee(new EmployeeCreateRequest(
            "EMP-PAGE-3", "Third", "Employee", "page3@example.com", "US", "Operations",
            "Coordinator", "ACTIVE", LocalDate.of(2024, 1, 1)
        ));

        var page = employeeService.listEmployees(PageRequest.of(
            0, 2, Sort.by(Sort.Direction.DESC, "employeeId")
        ));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent().get(0).employeeNumber()).isEqualTo("EMP-PAGE-3");
    }

    @Test
    void deleteEmployee_shouldDeleteEmployeeWithoutSalaryHistory() {
        var created = employeeService.createEmployee(new EmployeeCreateRequest(
            "EMP-DELETE-1", "Delete", "Me", "delete1@example.com", "US", "Operations",
            "Coordinator", "ACTIVE", LocalDate.of(2024, 1, 1)
        ));

        employeeService.deleteEmployee(created.id());

        assertThat(employeeService.listEmployees()).isEmpty();
    }

    @Test
    void deleteEmployee_shouldRejectEmployeeWithSalaryHistory() {
        var employee = employeeService.createEmployee(new EmployeeCreateRequest(
            "EMP-DELETE-2", "Keep", "History", "delete2@example.com", "US", "Operations",
            "Coordinator", "ACTIVE", LocalDate.of(2024, 1, 1)
        ));
        salaryService.createSalaryChange(new SalaryChangeRequest(
            employee.id(),
            new BigDecimal("80000.00"),
            "USD",
            "MONTHLY",
            LocalDate.of(2026, 1, 16),
            null,
            "Initial salary"
        ));

        assertThatThrownBy(() -> employeeService.deleteEmployee(employee.id()))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(exception -> assertThat(((ResponseStatusException) exception).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT));
        assertThat(employeeService.getEmployee(employee.id()).id()).isEqualTo(employee.id());
    }

    @Test
    void salaryService_shouldRespectEffectiveDatesAndFutureSchedule() {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
            "EMP-3001",
            "Alex",
            "Brown",
            "alex.brown@example.com",
            "US",
            "Engineering",
            "Developer",
            "ACTIVE",
            LocalDate.of(2022, 1, 10)
        );

        var employee = employeeService.createEmployee(request);

        salaryService.createSalaryChange(new SalaryChangeRequest(
            employee.id(),
            new BigDecimal("100000.00"),
            "USD",
            "MONTHLY",
            LocalDate.of(2026, 1, 16),
            null,
            "Initial salary"
        ));

        salaryService.createSalaryChange(new SalaryChangeRequest(
            employee.id(),
            new BigDecimal("120000.00"),
            "USD",
            "MONTHLY",
            LocalDate.of(2026, 6, 1),
            null,
            "Future increase"
        ));

        var history = salaryService.getSalaryHistory(employee.id());
        assertThat(history).hasSize(2);

        var current = salaryService.getCurrentSalary(employee.id(), LocalDate.of(2026, 2, 15));
        assertThat(current.amount()).isEqualByComparingTo("100000.00");

        var future = salaryService.getCurrentSalary(employee.id(), LocalDate.of(2026, 7, 1));
        assertThat(future.amount()).isEqualByComparingTo("120000.00");

        var audits = salaryAuditRepository.findAll().stream()
            .sorted(Comparator.comparing(audit -> audit.getSalaryAuditId()))
            .toList();
        assertThat(audits).hasSize(2);
        assertThat(audits.get(0).getOldSalaryRecord()).isNull();
        assertThat(audits.get(1).getOldSalaryRecord().getSalaryRecordId())
            .isEqualTo(audits.get(0).getNewSalaryRecord().getSalaryRecordId());
    }

    @Test
    void onboardEmployee_shouldPersistInitialSalaryAndAuditTogether() {
        LocalDate hireDate = LocalDate.of(2022, 1, 10);
        var response = employeeService.onboardEmployee(new EmployeeOnboardingRequest(
            new EmployeeCreateRequest(
                "EMP-ONBOARD-1", "Jordan", "Lee", "jordan.lee@example.com", "US",
                "Engineering", "Developer", "ACTIVE", hireDate
            ),
            new InitialSalaryRequest(
                new BigDecimal("95000.00"),
                "usd",
                "annual",
                hireDate,
                "Initial compensation"
            )
        ));

        assertThat(response.employee().employeeNumber()).isEqualTo("EMP-ONBOARD-1");
        assertThat(response.initialSalary().amount()).isEqualByComparingTo("95000.00");
        assertThat(response.initialSalary().effectiveFrom()).isEqualTo(hireDate);
        assertThat(response.initialSalary().createdBy()).isNotBlank();
        assertThat(salaryAuditRepository.findAll())
            .singleElement()
            .satisfies(audit -> {
                assertThat(audit.getOldSalaryRecord()).isNull();
                assertThat(audit.getNewSalaryRecord().getSalaryAmount()).isEqualByComparingTo("95000.00");
            });
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void onboardEmployee_shouldRollbackEmployeeWhenInitialSalaryIsInvalid() {
        LocalDate hireDate = LocalDate.of(2022, 1, 10);
        assertThatThrownBy(() -> employeeService.onboardEmployee(new EmployeeOnboardingRequest(
            new EmployeeCreateRequest(
                "EMP-ONBOARD-2", "Morgan", "Lee", "morgan.lee@example.com", "US",
                "Engineering", "Developer", "ACTIVE", hireDate
            ),
            new InitialSalaryRequest(
                new BigDecimal("95000.00"),
                "USD",
                "ANNUAL",
                hireDate.minusDays(1),
                "Invalid initial compensation date"
            )
        ))).isInstanceOf(IllegalArgumentException.class);

        assertThat(employeeService.listEmployees()).isEmpty();
        assertThat(salaryRecordRepository.count()).isZero();
    }

    @Test
    void seedService_shouldCreateExactlyTenThousandEmployees() {
        seedService.seedLocalDemoData();
        assertThat(seedService.countEmployees()).isEqualTo(10000);
        assertThat(seedService.countSalaryRecords()).isGreaterThanOrEqualTo(10000);
    }
}
