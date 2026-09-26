package com.generated.ldesportsbar.model;

import java.math.BigDecimal;

public class Station {
  private Long id;
  private String stationNo;
  private String zoneName;
  private String status;
  private BigDecimal hourlyRate;
  private Long activeSessionId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getStationNo() {
    return stationNo;
  }

  public void setStationNo(String stationNo) {
    this.stationNo = stationNo;
  }

  public String getZoneName() {
    return zoneName;
  }

  public void setZoneName(String zoneName) {
    this.zoneName = zoneName;
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

  public Long getActiveSessionId() {
    return activeSessionId;
  }

  public void setActiveSessionId(Long activeSessionId) {
    this.activeSessionId = activeSessionId;
  }
}
