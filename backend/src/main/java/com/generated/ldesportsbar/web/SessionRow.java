package com.generated.ldesportsbar.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 上机会话查询的数据库行（列与组件一一对应），服务层再补扣费明细转为 SessionView。 */
public record SessionRow(
    Long id,
    Long memberId,
    Long seatId,
    Long reservationId,
    LocalDateTime startTime,
    LocalDateTime planEndTime,
    LocalDateTime endTime,
    String status,
    Integer chargedMinutes,
    String chargeSummary,
    String memberNo,
    String memberName,
    String seatNo,
    String seatArea,
    String seatType,
    BigDecimal hourlyRate,
    String reservationNo
) {

  public SessionView toView(List<ChargeView> charges) {
    return new SessionView(
        id,
        memberId,
        seatId,
        reservationId,
        startTime,
        planEndTime,
        endTime,
        status,
        chargedMinutes,
        chargeSummary,
        memberNo,
        memberName,
        seatNo,
        seatArea,
        seatType,
        hourlyRate,
        reservationNo,
        charges
    );
  }
}
