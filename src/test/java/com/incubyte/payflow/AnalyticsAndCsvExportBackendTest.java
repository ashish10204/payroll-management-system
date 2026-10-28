package com.incubyte.payflow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.payflow.employee.EmployeeCreateRequest;
import com.incubyte.payflow.employee.EmployeeService;
import com.incubyte.payflow.salary.SalaryChangeRequest;
import com.incubyte.payflow.salary.SalaryService;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AnalyticsAndCsvExportBackendTest.FixedBusinessClockConfig.class)
@Transactional
class AnalyticsAndCsvExportBackendTest {

    @TestConfiguration
    static class FixedBusinessClockConfig {
        @Bean
        Clock clock() {
            return Clock.fixed(Instant.parse("2026-01-15T00:00:00Z"), ZoneOffset.UTC);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalaryService salaryService;

    @BeforeEach
    void setUp() {
        salaryService.clearTestData();
        employeeService.clearTestData();
    }

    @Test
    void headcountAndCompensationReports_shouldReturnCorrectAggregates() throws Exception {
        createEmployee("EMP-1001", "US", "Engineering", "Active User", "ACTIVE", "USD", "MONTHLY", new BigDecimal("100000.00"));
        createEmployee("EMP-1002", "US", "Engineering", "Inactive User", "INACTIVE", "USD", "MONTHLY", new BigDecimal("80000.00"));
        createEmployee("EMP-1003", "IN", "Finance", "Indian User", "ACTIVE", "INR", "MONTHLY", new BigDecimal("250000.00"));

        String headcountJson = mockMvc.perform(get("/api/v1/reports/headcount")
                .with(httpBasic("system", "system")))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

        JsonNode headcount = new ObjectMapper().readTree(headcountJson);
        assertThat(headcount.get("totalEmployees").asInt()).isEqualTo(3);
        assertThat(headcount.get("byCountry").size()).isGreaterThanOrEqualTo(2);

        String compensationJson = mockMvc.perform(get("/api/v1/reports/compensation")
                .with(httpBasic("system", "system")))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString(StandardCharsets.UTF_8);

        JsonNode compensation = new ObjectMapper().readTree(compensationJson);
        assertThat(compensation.get("groups").size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void csvExports_shouldBeProtectedAndEscapeFormulaInjection() throws Exception {
        createEmployee("EMP-2001", "US", "Engineering", "=SUM(1+1)", "ACTIVE", "USD", "MONTHLY", new BigDecimal("120000.00"));

        mockMvc.perform(get("/api/v1/reports/employees/export")
                .with(httpBasic("system", "system")))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.parseMediaType("text/csv")))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("employee_number")))
            .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("=SUM(1+1)"))));

        mockMvc.perform(get("/api/v1/reports/employees/export"))
            .andExpect(status().isUnauthorized());
    }

    private void createEmployee(String employeeNumber, String countryCode, String departmentCode,
                               String firstName, String employmentStatus, String currencyCode,
                               String payFrequency, BigDecimal amount) {
        EmployeeCreateRequest request = new EmployeeCreateRequest(
            employeeNumber,
            firstName,
            "Example",
            employeeNumber.toLowerCase() + "@payflow.local",
            countryCode,
            departmentCode,
            "Engineer",
            employmentStatus,
            LocalDate.of(2023, 1, 10)
        );

        var created = employeeService.createEmployee(request);
        salaryService.createSalaryChange(new SalaryChangeRequest(
            created.id(),
            amount,
            currencyCode,
            payFrequency,
            LocalDate.of(2026, 1, 16),
            null,
            "Initial salary"
        ));
    }
}
