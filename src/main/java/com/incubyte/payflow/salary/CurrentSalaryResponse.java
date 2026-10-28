package com.incubyte.payflow.salary;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CurrentSalaryResponse(
    Long employeeId,
    BigDecimal amount,
    String currencyCode,
    String payFrequency,
    LocalDate effectiveFrom,
    LocalDate effectiveTo,
    String reason
) {
}
