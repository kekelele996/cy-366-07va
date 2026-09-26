package com.generated.ldesportsbar.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.generated.ldesportsbar.domain.Seat;
import com.generated.ldesportsbar.mapper.SeatMapper;
import com.generated.ldesportsbar.service.OperationQueryService;
import com.generated.ldesportsbar.web.SessionView;

/** 运营台：查看机位状态、上机中的机位以及本次开机扣费。 */
@RestController
@RequestMapping({"/console", "/api/console"})
public class OperationConsoleController {

  private final OperationQueryService queryService;
  private final SeatMapper seatMapper;

  public OperationConsoleController(OperationQueryService queryService, SeatMapper seatMapper) {
    this.queryService = queryService;
    this.seatMapper = seatMapper;
  }

  @GetMapping("/seats")
  public List<Seat> seats() {
    return seatMapper.findAll();
  }

  /** 上机中的机位（含会员、机位、首小时扣费明细）。 */
  @GetMapping("/sessions/active")
  public List<SessionView> activeSessions() {
    return queryService.listActiveSessions();
  }

  /** 最近上机与扣费记录。 */
  @GetMapping("/sessions/recent")
  public List<SessionView> recentSessions() {
    return queryService.listRecentSessions();
  }
}
