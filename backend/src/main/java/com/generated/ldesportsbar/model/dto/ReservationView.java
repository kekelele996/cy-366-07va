package com.generated.ldesportsbar.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 前台「到店开机」可选择的预约单视图。
 */
public record ReservationView(
  Long reservationId,
  String reservationNo,
  Long memberId,
  String memberNo,
  String memberName,
  String memberPhone,
  BigDecimal balance,
  Integer packageMinutes,
  Long stationId,
  String stationNo,
  String zoneName,
  String stationStatus,
  BigDecimal hourlyRate,
  LocalDateTime startTime,
  LocalDateTime endTime,
  String status
) {
}
