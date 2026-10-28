package com.incubyte.payflow.salary;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SalaryHistoryItem(
    Long salaryRecordId,
    Long employeeId,
    BigDecimal amount,
    String currencyCode,
    String payFrequency,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    String reason,
    String createdBy
) {
}
