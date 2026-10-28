package com.incubyte.payflow.reporting;

import java.util.List;

public record CompensationReportResponse(
    long totalEmployees,
    long matchedEmployees,
    String currencyCode,
    String payFrequency,
    List<CompensationGroupSummary> groups
) {
}
