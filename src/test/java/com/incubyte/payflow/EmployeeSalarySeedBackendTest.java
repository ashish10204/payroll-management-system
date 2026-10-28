package com.incubyte.payflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.incubyte.payflow.employee.EmployeeCreateRequest;
import com.incubyte.payflow.employee.EmployeeService;
import com.incubyte.payflow.employee.EmployeeUpdateRequest;
import com.incubyte.payflow.salary.SalaryChangeRequest;
import com.incubyte.payflow.salary.SalaryService;
import com.incubyte.payflow.seed.SeedService;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
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
    }

    @Test
    void seedService_shouldCreateExactlyTenThousandEmployees() {
        seedService.seedLocalDemoData();
        assertThat(seedService.countEmployees()).isEqualTo(10000);
        assertThat(seedService.countSalaryRecords()).isGreaterThanOrEqualTo(10000);
    }
}
