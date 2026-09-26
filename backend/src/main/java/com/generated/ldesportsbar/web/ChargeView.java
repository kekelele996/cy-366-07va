package com.generated.ldesportsbar.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ChargeView(
    Long id,
    String chargeType,
    Integer minutes,
    BigDecimal amount,
    LocalDateTime createdAt
) {
}
