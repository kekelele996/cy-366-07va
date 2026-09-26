package com.generated.ldesportsbar.controller;

import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.generated.ldesportsbar.model.dto.ActiveStationView;
import com.generated.ldesportsbar.model.dto.ChargeView;
import com.generated.ldesportsbar.model.dto.CheckInRequest;
import com.generated.ldesportsbar.model.dto.CheckInResult;
import com.generated.ldesportsbar.model.dto.ReservationView;
import com.generated.ldesportsbar.service.FrontDeskService;

@RestController
@RequestMapping({"/front-desk", "/api/front-desk"})
public class FrontDeskController {

  private final FrontDeskService frontDeskService;

  public FrontDeskController(FrontDeskService frontDeskService) {
    this.frontDeskService = frontDeskService;
  }

  /** 到店预约单列表：前台选择预约单后核验开机。 */
  @GetMapping("/reservations")
  public List<ReservationView> reservations() {
    return frontDeskService.listArrivals();
  }

  /** 到店开机：核验会员与机位，扣首小时，写上机记录，机位切使用中。 */
  @PostMapping("/check-in")
  public CheckInResult checkIn(@Valid @RequestBody CheckInRequest request) {
    return frontDeskService.checkIn(request.getReservationId());
  }

  /** 运营台：上机中的机位。 */
  @GetMapping("/active-stations")
  public List<ActiveStationView> activeStations() {
    return frontDeskService.listActiveStations();
  }

  /** 运营台：上机扣费明细（默认最近 50 条）。 */
  @GetMapping("/charges")
  public Map<String, List<ChargeView>> charges(
      @RequestParam(name = "limit", defaultValue = "50") int limit) {
    return Map.of("charges", frontDeskService.listRecentCharges(limit));
  }
}
