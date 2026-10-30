package com.incubyte.payflow.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InitialSalaryRequest(
    BigDecimal amount,
    String currencyCode,
    String payFrequency,
    LocalDate effectiveFrom,
    String reason
) {
}
