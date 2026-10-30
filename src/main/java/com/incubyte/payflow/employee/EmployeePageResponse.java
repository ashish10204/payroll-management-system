package com.incubyte.payflow.employee;

import java.util.List;

public record EmployeePageResponse(
    List<EmployeeResponse> content,
    long totalElements,
    int totalPages,
    int number,
    int size
) {
}
