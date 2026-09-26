package com.generated.ldesportsbar.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.generated.ldesportsbar.domain.ChargeRecord;
import com.generated.ldesportsbar.mapper.ChargeRecordMapper;
import com.generated.ldesportsbar.mapper.ReservationMapper;
import com.generated.ldesportsbar.mapper.SeatMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.mapper.UsageSessionMapper;
import com.generated.ldesportsbar.web.ChargeView;
import com.generated.ldesportsbar.web.ReservationRow;
import com.generated.ldesportsbar.web.ReservationView;
import com.generated.ldesportsbar.web.SessionRow;
import com.generated.ldesportsbar.web.SessionView;

@Service
public class OperationQueryService {

  private static final int RECENT_SESSION_LIMIT = 50;

  private final ReservationMapper reservationMapper;
  private final SeatMapper seatMapper;
  private final TimePackageMapper timePackageMapper;
  private final UsageSessionMapper usageSessionMapper;
  private final ChargeRecordMapper chargeRecordMapper;

  public OperationQueryService(ReservationMapper reservationMapper,
                               SeatMapper seatMapper,
                               TimePackageMapper timePackageMapper,
                               UsageSessionMapper usageSessionMapper,
                               ChargeRecordMapper chargeRecordMapper) {
    this.reservationMapper = reservationMapper;
    this.seatMapper = seatMapper;
    this.timePackageMapper = timePackageMapper;
    this.usageSessionMapper = usageSessionMapper;
    this.chargeRecordMapper = chargeRecordMapper;
  }

  /** 前台可选预约单（默认只看待开机的 RESERVED），并附带会员可用时长包分钟数。 */
  @Transactional(readOnly = true)
  public List<ReservationView> listReservations(String status) {
    LocalDateTime now = LocalDateTime.now();
    return reservationMapper.findRows(status).stream()
        .map(row -> row.toView(timePackageMapper.sumUsableMinutes(row.memberId(), now)))
        .toList();
  }

  @Transactional(readOnly = true)
  public ReservationView getReservation(Long id) {
    ReservationRow row = reservationMapper.findRowById(id);
    if (row == null) {
      return null;
    }
    return row.toView(timePackageMapper.sumUsableMinutes(row.memberId(), LocalDateTime.now()));
  }

  /** 运营台：上机中的机位 + 本次扣费明细。 */
  @Transactional(readOnly = true)
  public List<SessionView> listActiveSessions() {
    return usageSessionMapper.findRowsByStatus("IN_USE").stream()
        .map(this::withCharges)
        .toList();
  }

  /** 运营台：最近上机记录（含本次扣费），用于回看开机扣费。 */
  @Transactional(readOnly = true)
  public List<SessionView> listRecentSessions() {
    return usageSessionMapper.findRecentRows(RECENT_SESSION_LIMIT).stream()
        .map(this::withCharges)
        .toList();
  }

  private SessionView withCharges(SessionRow row) {
    List<ChargeView> charges = chargeRecordMapper.findBySessionId(row.id()).stream()
        .map(this::toChargeView)
        .toList();
    return row.toView(charges);
  }

  private ChargeView toChargeView(ChargeRecord record) {
    return new ChargeView(record.getId(), record.getChargeType(), record.getMinutes(),
        record.getAmount(), record.getCreatedAt());
  }
}
