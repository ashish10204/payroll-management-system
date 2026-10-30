package com.incubyte.payflow.employee;

import com.incubyte.payflow.salary.SalaryHistoryItem;

public record EmployeeOnboardingResponse(
    EmployeeResponse employee,
    SalaryHistoryItem initialSalary
) {
}
