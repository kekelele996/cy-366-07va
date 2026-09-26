package com.generated.ldesportsbar.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Session {
  private Long id;
  private String sessionNo;
  private Long reservationId;
  private Long memberId;
  private Long stationId;
  private LocalDateTime startedAt;
  private LocalDateTime endedAt;
  private Integer packageMinutesUsed;
  private BigDecimal balanceAmountUsed;
  private BigDecimal firstHourCharge;
  private String status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSessionNo() {
    return sessionNo;
  }

  public void setSessionNo(String sessionNo) {
    this.sessionNo = sessionNo;
  }

  public Long getReservationId() {
    return reservationId;
  }

  public void setReservationId(Long reservationId) {
    this.reservationId = reservationId;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }

  public LocalDateTime getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(LocalDateTime startedAt) {
    this.startedAt = startedAt;
  }

  public LocalDateTime getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(LocalDateTime endedAt) {
    this.endedAt = endedAt;
  }

  public Integer getPackageMinutesUsed() {
    return packageMinutesUsed;
  }

  public void setPackageMinutesUsed(Integer packageMinutesUsed) {
    this.packageMinutesUsed = packageMinutesUsed;
  }

  public BigDecimal getBalanceAmountUsed() {
    return balanceAmountUsed;
  }

  public void setBalanceAmountUsed(BigDecimal balanceAmountUsed) {
    this.balanceAmountUsed = balanceAmountUsed;
  }

  public BigDecimal getFirstHourCharge() {
    return firstHourCharge;
  }

  public void setFirstHourCharge(BigDecimal firstHourCharge) {
    this.firstHourCharge = firstHourCharge;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
