package com.incubyte.payflow.employee;

import java.time.LocalDate;

public record EmployeeCreateRequest(
    String employeeNumber,
    String firstName,
    String lastName,
    String email,
    String countryCode,
    String departmentCode,
    String jobTitle,
    String employmentStatus,
    LocalDate hireDate
) {
}
