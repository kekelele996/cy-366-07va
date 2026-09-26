package com.generated.ldesportsbar.domain;

import java.time.LocalDateTime;

public class UsageSession {
  private Long id;
  private Long memberId;
  private Long seatId;
  private Long reservationId;
  private LocalDateTime startTime;
  private LocalDateTime planEndTime;
  private LocalDateTime endTime;
  /** IN_USE / FINISHED */
  private String status;
  private Integer chargedMinutes;
  private String chargeSummary;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public Long getSeatId() {
    return seatId;
  }

  public void setSeatId(Long seatId) {
    this.seatId = seatId;
  }

  public Long getReservationId() {
    return reservationId;
  }

  public void setReservationId(Long reservationId) {
    this.reservationId = reservationId;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getPlanEndTime() {
    return planEndTime;
  }

  public void setPlanEndTime(LocalDateTime planEndTime) {
    this.planEndTime = planEndTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getChargedMinutes() {
    return chargedMinutes;
  }

  public void setChargedMinutes(Integer chargedMinutes) {
    this.chargedMinutes = chargedMinutes;
  }

  public String getChargeSummary() {
    return chargeSummary;
  }

  public void setChargeSummary(String chargeSummary) {
    this.chargeSummary = chargeSummary;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
