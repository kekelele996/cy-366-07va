package com.generated.ldesportsbar.domain;

import java.time.LocalDateTime;

public class TimePackage {
  private Long id;
  private Long memberId;
  private String packageName;
  private Integer totalMinutes;
  private Integer remainingMinutes;
  private LocalDateTime expireAt;
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

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
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

  public LocalDateTime getExpireAt() {
    return expireAt;
  }

  public void setExpireAt(LocalDateTime expireAt) {
    this.expireAt = expireAt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
