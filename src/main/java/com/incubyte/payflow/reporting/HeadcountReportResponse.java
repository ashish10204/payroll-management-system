package com.incubyte.payflow.reporting;

import java.util.Map;

public record HeadcountReportResponse(
    long totalEmployees,
    Map<String, Long> byCountry,
    Map<String, Long> byDepartment,
    Map<String, Long> byEmploymentStatus
) {
}
