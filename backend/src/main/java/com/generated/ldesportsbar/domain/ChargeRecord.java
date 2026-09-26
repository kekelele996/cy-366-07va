package com.generated.ldesportsbar.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ChargeRecord {
  private Long id;
  private Long sessionId;
  private Long memberId;
  /** PACKAGE 时长包扣减 / BALANCE 余额扣减 */
  private String chargeType;
  private Integer minutes;
  private BigDecimal amount;
  private LocalDateTime createdAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public Long getMemberId() {
    return memberId;
  }

  public void setMemberId(Long memberId) {
    this.memberId = memberId;
  }

  public String getChargeType() {
    return chargeType;
  }

  public void setChargeType(String chargeType) {
    this.chargeType = chargeType;
  }

  public Integer getMinutes() {
    return minutes;
  }

  public void setMinutes(Integer minutes) {
    this.minutes = minutes;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
