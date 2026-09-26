package com.generated.ldesportsbar.controller;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.generated.ldesportsbar.exception.ApiException;
import com.generated.ldesportsbar.service.CheckinService;
import com.generated.ldesportsbar.service.OperationQueryService;
import com.generated.ldesportsbar.web.CheckinResult;
import com.generated.ldesportsbar.web.ReservationView;

/** 前台到店开机：选择预约单 -> 核验会员与机位 -> 扣首小时 -> 机位切使用中。 */
@RestController
@RequestMapping({"/front", "/api/front"})
public class FrontDeskController {

  private final OperationQueryService queryService;
  private final CheckinService checkinService;

  public FrontDeskController(OperationQueryService queryService, CheckinService checkinService) {
    this.queryService = queryService;
    this.checkinService = checkinService;
  }

  /** 前台可见的预约单，默认只看待开机（RESERVED）。 */
  @GetMapping("/reservations")
  public List<ReservationView> reservations(
      @RequestParam(name = "status", required = false) String status) {
    return queryService.listReservations(status == null || status.isBlank() ? "RESERVED" : status);
  }

  @GetMapping("/reservations/{id}")
  public ReservationView reservation(@PathVariable Long id) {
    ReservationView view = queryService.getReservation(id);
    if (view == null) {
      throw new ApiException("预约单不存在，请确认后重试。");
    }
    return view;
  }

  /** 到店开机。核验/状态问题返回 400；余额不足返回 200 且 success=false（预约与机位保持原状，带差额提示）。 */
  @PostMapping("/reservations/{id}/checkin")
  public CheckinResult checkin(@PathVariable Long id) {
    return checkinService.checkin(id);
  }

  /** 简单的健康自检入口，方便部署确认。 */
  @GetMapping("/ping")
  public Map<String, String> ping() {
    return Map.of("module", "front-desk", "status", "ok");
  }
}
