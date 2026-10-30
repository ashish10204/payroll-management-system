package com.incubyte.payflow.reporting;

import com.incubyte.payflow.domain.Employee;
import com.incubyte.payflow.domain.SalaryRecord;
import com.incubyte.payflow.employee.EmployeeResponse;
import com.incubyte.payflow.employee.EmployeeService;
import com.incubyte.payflow.repository.EmployeeRepository;
import com.incubyte.payflow.repository.SalaryRecordRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportingService {

    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final EmployeeService employeeService;

    public ReportingService(EmployeeRepository employeeRepository,
                           SalaryRecordRepository salaryRecordRepository,
                           EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.employeeService = employeeService;
    }

    @Transactional(readOnly = true)
    public HeadcountReportResponse headcount(String countryCode,
                                            String departmentCode,
                                            String jobTitle,
                                            String employmentStatus) {
        List<Employee> employees = employeeRepository.findAll().stream()
            .filter(e -> matchesCountry(e, countryCode))
            .filter(e -> matchesDepartment(e, departmentCode))
            .filter(e -> matchesJobTitle(e, jobTitle))
            .filter(e -> matchesEmploymentStatus(e, employmentStatus))
            .toList();

        return new HeadcountReportResponse(
            employees.size(),
            toCounts(employees, e -> e.getCountry() == null ? "UNKNOWN" : e.getCountry().getCountryCode()),
            toCounts(employees, e -> e.getDepartment() == null ? "UNKNOWN" : e.getDepartment().getDepartmentCode()),
            toCounts(employees, e -> e.getEmploymentStatus() == null ? "UNKNOWN" : e.getEmploymentStatus().name())
        );
    }

    @Transactional(readOnly = true)
    public CompensationReportResponse compensation(String countryCode,
                                                  String departmentCode,
                                                  String jobTitle,
                                                  String employmentStatus,
                                                  String currencyCode,
                                                  String payFrequency,
                                                  LocalDate asOfDate) {
        LocalDate effectiveDate = asOfDate == null ? LocalDate.now() : asOfDate;
        List<SalaryRecord> currentRecords = salaryRecordRepository.findAll().stream()
            .filter(r -> r.getEffectiveFrom() != null && !r.getEffectiveFrom().isAfter(effectiveDate))
            .filter(r -> r.getEffectiveTo() == null || !r.getEffectiveTo().isBefore(effectiveDate))
            .filter(r -> matchesCountry(r.getEmployee(), countryCode))
            .filter(r -> matchesDepartment(r.getEmployee(), departmentCode))
            .filter(r -> matchesJobTitle(r.getEmployee(), jobTitle))
            .filter(r -> matchesEmploymentStatus(r.getEmployee(), employmentStatus))
            .filter(r -> matchesCurrency(r.getCurrencyCode(), currencyCode))
            .filter(r -> matchesPayFrequency(r.getPayFrequency().name(), payFrequency))
            .sorted(Comparator.comparing((SalaryRecord record) -> record.getEmployee().getEmployeeId())
                .thenComparing(SalaryRecord::getEffectiveFrom).reversed())
            .collect(Collectors.toList());

        List<SalaryRecord> deduplicated = new ArrayList<>();
        Long lastEmployeeId = null;
        for (SalaryRecord record : currentRecords) {
            if (!Objects.equals(lastEmployeeId, record.getEmployee().getEmployeeId())) {
                deduplicated.add(record);
                lastEmployeeId = record.getEmployee().getEmployeeId();
            }
        }

        if (deduplicated.isEmpty()) {
            return new CompensationReportResponse(0, 0, currencyCode, payFrequency, List.of());
        }

        Map<String, List<SalaryRecord>> grouped = deduplicated.stream()
            .collect(Collectors.groupingBy(r -> r.getCurrencyCode() + "|" + r.getPayFrequency().name(), Collectors.toList()));

        List<CompensationGroupSummary> groups = grouped.entrySet().stream()
            .map(entry -> {
                List<SalaryRecord> records = entry.getValue();
                BigDecimal[] amounts = records.stream().map(SalaryRecord::getSalaryAmount).toArray(BigDecimal[]::new);
                BigDecimal total = records.stream().map(SalaryRecord::getSalaryAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal min = records.stream().map(SalaryRecord::getSalaryAmount).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                BigDecimal max = records.stream().map(SalaryRecord::getSalaryAmount).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
                BigDecimal average = total.divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
                String[] parts = entry.getKey().split("\\|");
                return new CompensationGroupSummary(parts[0], parts[1], records.size(), min, average, max, total);
            })
            .sorted(Comparator.comparing(CompensationGroupSummary::currencyCode)
                .thenComparing(CompensationGroupSummary::payFrequency))
            .toList();

        return new CompensationReportResponse(
            employeeRepository.count(),
            deduplicated.size(),
            currencyCode,
            payFrequency,
            groups
        );
    }

    public String exportEmployeesCsv(String countryCode,
                                   String departmentCode,
                                   String jobTitle,
                                   String employmentStatus) {
        List<EmployeeResponse> employees = employeeService.listEmployees().stream()
            .filter(e -> matchesString(e.countryCode(), countryCode))
            .filter(e -> matchesString(e.departmentCode(), departmentCode))
            .filter(e -> matchesString(e.jobTitle(), jobTitle))
            .filter(e -> matchesString(e.employmentStatus(), employmentStatus))
            .toList();

        StringBuilder csv = new StringBuilder();
        csv.append("employee_number,first_name,last_name,email,country_code,department_code,job_title,employment_status,hire_date\n");
        for (EmployeeResponse employee : employees) {
            csv.append(escapeCsv(employee.employeeNumber())).append(',')
                .append(escapeCsv(employee.firstName())).append(',')
                .append(escapeCsv(employee.lastName())).append(',')
                .append(escapeCsv(employee.email())).append(',')
                .append(escapeCsv(employee.countryCode())).append(',')
                .append(escapeCsv(employee.departmentCode())).append(',')
                .append(escapeCsv(employee.jobTitle())).append(',')
                .append(escapeCsv(employee.employmentStatus())).append(',')
                .append(escapeCsv(employee.hireDate() == null ? "" : employee.hireDate().toString()))
                .append('\n');
        }
        return csv.toString();
    }

    private Map<String, Long> toCounts(List<Employee> employees, java.util.function.Function<Employee, String> classifier) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Employee employee : employees) {
            String classification = classifier.apply(employee);
            counts.merge(classification, 1L, Long::sum);
        }
        return counts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));
    }

    private boolean matchesCountry(Employee employee, String countryCode) {
        return countryCode == null || countryCode.isBlank() || (employee.getCountry() != null && employee.getCountry().getCountryCode().equalsIgnoreCase(countryCode));
    }

    private boolean matchesDepartment(Employee employee, String departmentCode) {
        return departmentCode == null || departmentCode.isBlank() || (employee.getDepartment() != null && employee.getDepartment().getDepartmentCode().equalsIgnoreCase(departmentCode));
    }

    private boolean matchesJobTitle(Employee employee, String jobTitle) {
        return jobTitle == null || jobTitle.isBlank() || employee.getJobTitle() != null && employee.getJobTitle().equalsIgnoreCase(jobTitle);
    }

    private boolean matchesEmploymentStatus(Employee employee, String employmentStatus) {
        return employmentStatus == null || employmentStatus.isBlank() || employee.getEmploymentStatus() != null && employee.getEmploymentStatus().name().equalsIgnoreCase(employmentStatus);
    }

    private boolean matchesCurrency(String currency, String expected) {
        return expected == null || expected.isBlank() || currency.equalsIgnoreCase(expected);
    }

    private boolean matchesPayFrequency(String frequency, String expected) {
        return expected == null || expected.isBlank() || frequency.equalsIgnoreCase(expected);
    }

    private boolean matchesString(String value, String expected) {
        return expected == null || expected.isBlank() || (value != null && value.equalsIgnoreCase(expected));
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String sanitized = value.replace("\r", " ").replace("\n", " ").replace("\t", " ");
        if (sanitized.startsWith("=") || sanitized.startsWith("+") || sanitized.startsWith("-") || sanitized.startsWith("@")) {
            sanitized = sanitized.substring(1);
        }
        if (sanitized.contains(",") || sanitized.contains("\"") || sanitized.contains("\n") || sanitized.contains("\r")) {
            sanitized = '"' + sanitized.replace("\"", "\"\"") + '"';
        }
        return sanitized;
    }
}
