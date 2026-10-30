package com.incubyte.payflow.seed;

import com.incubyte.payflow.domain.Country;
import com.incubyte.payflow.domain.Department;
import com.incubyte.payflow.domain.Employee;
import com.incubyte.payflow.domain.EmploymentStatus;
import com.incubyte.payflow.domain.PayFrequency;
import com.incubyte.payflow.domain.SalaryRecord;
import com.incubyte.payflow.repository.CountryRepository;
import com.incubyte.payflow.repository.DepartmentRepository;
import com.incubyte.payflow.repository.EmployeeRepository;
import com.incubyte.payflow.repository.SalaryAuditRepository;
import com.incubyte.payflow.repository.SalaryRecordRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class SeedService {

    private static final int TARGET_EMPLOYEE_COUNT = 10_000;

    private final CountryRepository countryRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRecordRepository salaryRecordRepository;
    private final SalaryAuditRepository salaryAuditRepository;
    private final EntityManager entityManager;

    public SeedService(CountryRepository countryRepository,
                      DepartmentRepository departmentRepository,
                      EmployeeRepository employeeRepository,
                      SalaryRecordRepository salaryRecordRepository,
                      SalaryAuditRepository salaryAuditRepository,
                      EntityManager entityManager) {
        this.countryRepository = countryRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.salaryRecordRepository = salaryRecordRepository;
        this.salaryAuditRepository = salaryAuditRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public void seedLocalDemoData() {
        if (employeeRepository.count() >= TARGET_EMPLOYEE_COUNT) {
            return;
        }

        salaryAuditRepository.deleteAll();
        salaryRecordRepository.deleteAll();
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        countryRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        List<Country> countries = List.of(
            new Country("US", "United States", "USD"),
            new Country("IN", "India", "INR"),
            new Country("GB", "United Kingdom", "GBP"),
            new Country("DE", "Germany", "EUR"),
            new Country("AU", "Australia", "AUD")
        );
        countries = countryRepository.saveAllAndFlush(countries);

        List<Department> departments = List.of(
            new Department("ENG", "Engineering"),
            new Department("FIN", "Finance"),
            new Department("HR", "Human Resources"),
            new Department("OPS", "Operations"),
            new Department("SALES", "Sales")
        );
        departments = departmentRepository.saveAllAndFlush(departments);

        List<Employee> employees = new ArrayList<>();
        List<SalaryRecord> salaryRecords = new ArrayList<>();

        for (int i = 1; i <= TARGET_EMPLOYEE_COUNT; i++) {
            Country country = countries.get(i % countries.size());
            Department department = departments.get((i - 1) % departments.size());
            String employeeNumber = String.format(Locale.US, "EMP-%05d", i);
            String firstName = "Employee" + i;
            String lastName = "Seed" + (i % 97);
            String email = "employee" + i + "@payflow.local";
            EmploymentStatus status = i % 7 == 0 ? EmploymentStatus.INACTIVE : EmploymentStatus.ACTIVE;

            Employee employee = new Employee(
                employeeNumber,
                firstName,
                lastName,
                email,
                country,
                department,
                "Role " + (i % 10),
                status,
                LocalDate.of(2018, 1, 1).plusDays(i % 2000)
            );
            employees.add(employee);

            BigDecimal amount = BigDecimal.valueOf(40000 + (i % 1000) * 2500L);
            SalaryRecord salaryRecord = new SalaryRecord(
                employee,
                amount,
                country.getDefaultCurrencyCode(),
                i % 3 == 0 ? PayFrequency.ANNUAL : PayFrequency.MONTHLY,
                LocalDate.of(2024, 1, 1),
                null,
                "Seeded current salary",
                "system"
            );
            salaryRecords.add(salaryRecord);
        }

        employeeRepository.saveAll(employees);
        salaryRecordRepository.saveAll(salaryRecords);
    }

    public int countEmployees() {
        return (int) employeeRepository.count();
    }

    public int countSalaryRecords() {
        return (int) salaryRecordRepository.count();
    }
}
