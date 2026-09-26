package com.generated.ldesportsbar.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 运营台上机记录视图：机位、会员、本次扣费明细。 */
public record SessionView(
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
    String reservationNo,
    List<ChargeView> charges
) {
}
