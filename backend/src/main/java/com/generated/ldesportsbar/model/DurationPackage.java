package com.generated.ldesportsbar.model;

import java.time.LocalDateTime;

public class DurationPackage {
  private Long id;
  private Long memberId;
  private String name;
  private Integer totalMinutes;
  private Integer remainingMinutes;
  private LocalDateTime validFrom;
  private LocalDateTime validUntil;
  private String status;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getTotalMinutes() {
    return totalMinutes;
  }

  public void setTotalMinutes(Integer totalMinutes) {
    this.totalMinutes = totalMinutes;
  }

  public Integer getRemainingMinutes() {
    return remainingMinutes;
  }

  public void setRemainingMinutes(Integer remainingMinutes) {
    this.remainingMinutes = remainingMinutes;
  }

  public LocalDateTime getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(LocalDateTime validFrom) {
    this.validFrom = validFrom;
  }

  public LocalDateTime getValidUntil() {
    return validUntil;
  }

  public void setValidUntil(LocalDateTime validUntil) {
    this.validUntil = validUntil;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
