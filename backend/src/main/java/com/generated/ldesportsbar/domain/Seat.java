package com.generated.ldesportsbar.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Seat {
  private Long id;
  private String seatNo;
  private String area;
  private String zone;
  private String seatType;
  /** IDLE / IN_USE / RESERVED / FAULT */
  private String status;
  private BigDecimal hourlyRate;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSeatNo() {
    return seatNo;
  }

  public void setSeatNo(String seatNo) {
    this.seatNo = seatNo;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public String getSeatType() {
    return seatType;
  }

  public void setSeatType(String seatType) {
    this.seatType = seatType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BigDecimal getHourlyRate() {
    return hourlyRate;
  }

  public void setHourlyRate(BigDecimal hourlyRate) {
    this.hourlyRate = hourlyRate;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
