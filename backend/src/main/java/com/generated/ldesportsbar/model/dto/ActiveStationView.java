package com.generated.ldesportsbar.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 运营台「上机中」机位视图。
 */
public record ActiveStationView(
  Long sessionId,
  String sessionNo,
  Long stationId,
  String stationNo,
  String zoneName,
  Long memberId,
  String memberNo,
  String memberName,
  String reservationNo,
  LocalDateTime startedAt,
  BigDecimal firstHourCharge,
  Integer packageMinutesUsed,
  BigDecimal balanceAmountUsed
) {
}
