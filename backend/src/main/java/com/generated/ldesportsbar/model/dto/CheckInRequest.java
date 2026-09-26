package com.generated.ldesportsbar.model.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 到店开机请求：前台核验后选择预约单开机。
 */
public class CheckInRequest {
  @NotNull(message = "预约单不能为空")
  private Long reservationId;

  public Long getReservationId() {
    return reservationId;
  }

  public void setReservationId(Long reservationId) {
    this.reservationId = reservationId;
  }
}
