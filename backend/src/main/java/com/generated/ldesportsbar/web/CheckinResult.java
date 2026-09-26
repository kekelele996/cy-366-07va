package com.generated.ldesportsbar.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** 到店开机结果。success=false 时预约与机位均保持原状，shortfall 为需要补足的差额。 */
public record CheckinResult(
    boolean success,
    String message,
    Long reservationId,
    String reservationNo,
    Long sessionId,
    Long memberId,
    String memberName,
    Long seatId,
    String seatNo,
    LocalDateTime startTime,
    LocalDateTime planEndTime,
    Integer firstHourMinutes,
    BigDecimal firstHourFee,
    Integer packageMinutesUsed,
    BigDecimal balanceUsed,
    BigDecimal shortfall,
    BigDecimal remainingBalance,
    Integer remainingPackageMinutes,
    String seatStatus,
    List<ChargeDetail> charges
) {

  /** 余额不足等核验失败结果：不写入任何数据，保留预约和原机位状态。 */
  public static CheckinResult rejected(String message,
                                      Long reservationId,
                                      String reservationNo,
                                      Long memberId,
                                      String memberName,
                                      Long seatId,
                                      String seatNo,
                                      BigDecimal firstHourFee,
                                      Integer packageMinutesUsed,
                                      BigDecimal balanceAvailable,
                                      BigDecimal shortfall,
                                      Integer remainingPackageMinutes,
                                      List<ChargeDetail> wouldBeCharges) {
    return new CheckinResult(
        false,
        message,
        reservationId,
        reservationNo,
        null,
        memberId,
        memberName,
        seatId,
        seatNo,
        null,
        null,
        null,
        firstHourFee,
        packageMinutesUsed,
        BigDecimal.ZERO.setScale(2),
        shortfall,
        balanceAvailable,
        remainingPackageMinutes,
        null,
        wouldBeCharges
    );
  }
}
