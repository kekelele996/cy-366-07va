package com.generated.ldesportsbar.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 预约单列表/详情视图：含会员与机位核验所需的信息。 */
public record ReservationView(
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
    BigDecimal hourlyRate,
    Integer packageRemainingMinutes
) {
}
