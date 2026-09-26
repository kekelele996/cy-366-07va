package com.generated.ldesportsbar.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 预约查询的数据库行（列与组件一一对应），服务层再补时长包分钟数转为 ReservationView。 */
public record ReservationRow(
    Long id,
    String reservationNo,
    Long memberId,
    Long seatId,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String status,
    String memberNo,
    String memberName,
    BigDecimal memberBalance,
    String seatNo,
    String seatArea,
    String seatType,
    String seatStatus,
    BigDecimal hourlyRate
) {

  public ReservationView toView(int packageRemainingMinutes) {
    return new ReservationView(
        id,
        reservationNo,
        memberId,
        seatId,
        startTime,
        endTime,
        status,
        memberNo,
        memberName,
        memberBalance,
        seatNo,
        seatArea,
        seatType,
        seatStatus,
        hourlyRate,
        packageRemainingMinutes
    );
  }
}
