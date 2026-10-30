package com.incubyte.payflow.employee;

public record EmployeeOnboardingRequest(
    EmployeeCreateRequest employee,
    InitialSalaryRequest initialSalary
) {
}
