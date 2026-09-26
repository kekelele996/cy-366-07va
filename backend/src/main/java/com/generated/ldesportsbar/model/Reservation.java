package com.generated.ldesportsbar.model;

import java.time.LocalDateTime;

public class Reservation {
  private Long id;
  private String reservationNo;
  private Long memberId;
  private Long stationId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String status;
  private LocalDateTime checkedInAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getReservationNo() {
    return reservationNo;
  }

  public void setReservationNo(String reservationNo) {
    this.reservationNo = reservationNo;
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

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
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

  public LocalDateTime getCheckedInAt() {
    return checkedInAt;
  }

  public void setCheckedInAt(LocalDateTime checkedInAt) {
    this.checkedInAt = checkedInAt;
  }
}
