package com.generated.ldesportsbar.web;

import java.math.BigDecimal;

/** 开机时的扣费明细一行：时长包或余额。 */
public record ChargeDetail(
    String chargeType,
    Integer minutes,
    BigDecimal amount
) {
}
