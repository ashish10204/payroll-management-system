package com.incubyte.payflow.reporting;

import java.math.BigDecimal;

public record CompensationGroupSummary(
    String currencyCode,
    String payFrequency,
    long employeeCount,
    BigDecimal minimumAmount,
    BigDecimal averageAmount,
    BigDecimal maximumAmount,
    BigDecimal totalAmount
) {
}
