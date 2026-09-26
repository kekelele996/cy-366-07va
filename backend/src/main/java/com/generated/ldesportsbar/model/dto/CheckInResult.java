package com.generated.ldesportsbar.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 到店开机结果。
 * success=false 且 code=INSUFFICIENT_FUNDS 时，预约和机位状态保持不变，
 * shortage 为时长包 + 余额合计仍差的金额（元），供前台提示充值。
 */
public record CheckInResult(
  boolean success,
  String code,
  String message,
  Long sessionId,
  String sessionNo,
  String memberName,
  String stationNo,
  LocalDateTime startedAt,
  BigDecimal firstHourCharge,
  Integer packageMinutesUsed,
  BigDecimal balanceAmountUsed,
  BigDecimal shortage,
  List<ChargeView> charges
) {

  public static CheckInResult insufficientFunds(String memberName, String stationNo,
      BigDecimal firstHourCharge, int packageMinutes, BigDecimal balance, BigDecimal shortage) {
    String message = String.format(
      "会员 %s 可用时长包 %d 分钟、余额 %.2f 元，仍不足以支付机位 %s 首小时 %.2f 元，差额 %.2f 元，请先充值后再开机。预约单与机位状态已保留。",
      memberName, packageMinutes, balance, stationNo, firstHourCharge, shortage);
    return new CheckInResult(false, "INSUFFICIENT_FUNDS", message, null, null,
      memberName, stationNo, null, firstHourCharge, 0, BigDecimal.ZERO.setScale(2), shortage,
      List.of());
  }
}
