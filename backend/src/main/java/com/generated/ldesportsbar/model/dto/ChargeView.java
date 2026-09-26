package com.generated.ldesportsbar.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 本次上机扣费明细（时长包扣减 + 余额扣款）。
 */
public record ChargeView(
  Long id,
  Long sessionId,
  Long memberId,
  String memberName,
  String stationNo,
  String chargeType,
  Integer minutes,
  BigDecimal amount,
  String detail,
  LocalDateTime createdAt
) {
}
