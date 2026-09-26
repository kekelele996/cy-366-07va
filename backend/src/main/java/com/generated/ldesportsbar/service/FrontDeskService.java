package com.generated.ldesportsbar.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.generated.ldesportsbar.exception.ApiException;
import com.generated.ldesportsbar.mapper.DurationPackageMapper;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.ReservationMapper;
import com.generated.ldesportsbar.mapper.SessionChargeMapper;
import com.generated.ldesportsbar.mapper.SessionMapper;
import com.generated.ldesportsbar.mapper.StationMapper;
import com.generated.ldesportsbar.model.DurationPackage;
import com.generated.ldesportsbar.model.Member;
import com.generated.ldesportsbar.model.Reservation;
import com.generated.ldesportsbar.model.Session;
import com.generated.ldesportsbar.model.SessionCharge;
import com.generated.ldesportsbar.model.Station;
import com.generated.ldesportsbar.model.dto.ActiveStationView;
import com.generated.ldesportsbar.model.dto.ChargeView;
import com.generated.ldesportsbar.model.dto.CheckInResult;
import com.generated.ldesportsbar.model.dto.ReservationView;

@Service
public class FrontDeskService {

  /** 首小时按 60 分钟扣减。 */
  private static final int FIRST_HOUR_MINUTES = 60;
  private static final String STATION_IN_USE = "in_use";
  private static final String RESERVATION_CHECKED_IN = "checked_in";

  private final ReservationMapper reservationMapper;
  private final StationMapper stationMapper;
  private final MemberMapper memberMapper;
  private final DurationPackageMapper packageMapper;
  private final SessionMapper sessionMapper;
  private final SessionChargeMapper chargeMapper;

  public FrontDeskService(ReservationMapper reservationMapper, StationMapper stationMapper,
      MemberMapper memberMapper, DurationPackageMapper packageMapper, SessionMapper sessionMapper,
      SessionChargeMapper chargeMapper) {
    this.reservationMapper = reservationMapper;
    this.stationMapper = stationMapper;
    this.memberMapper = memberMapper;
    this.packageMapper = packageMapper;
    this.sessionMapper = sessionMapper;
    this.chargeMapper = chargeMapper;
  }

  /** 前台可处理的到店预约单（含会员时长包、余额、机位信息）。 */
  public List<ReservationView> listArrivals() {
    return reservationMapper.findArrivals();
  }

  /** 运营台：上机中的机位。 */
  public List<ActiveStationView> listActiveStations() {
    return sessionMapper.findActiveStations();
  }

  /** 运营台：最近的上机扣费明细。 */
  public List<ChargeView> listRecentCharges(int limit) {
    return sessionMapper.findRecentCharges(Math.max(1, Math.min(limit, 200)));
  }

  /**
   * 到店开机：核验会员与机位，按时长包优先、余额兜底扣掉首小时，
   * 写入上机记录并把机位切成使用中。
   * 余额不足时不做任何改动，预约与机位状态保留，并返回差额提示。
   */
  @Transactional
  public CheckInResult checkIn(Long reservationId) {
    LocalDateTime now = LocalDateTime.now();

    Reservation reservation = reservationMapper.findById(reservationId);
    if (reservation == null) {
      throw new ApiException("预约单不存在，请向前台核实预约信息。");
    }
    if (!"reserved".equals(reservation.getStatus())) {
      throw new ApiException("预约单 " + reservation.getReservationNo() + " 当前状态为「"
          + reservationStatusLabel(reservation.getStatus()) + "」，不能重复开机。");
    }

    Member member = memberMapper.findById(reservation.getMemberId());
    if (member == null) {
      throw new ApiException("预约会员信息缺失，无法开机。");
    }
    if ("frozen".equals(member.getStatus())) {
      throw new ApiException("会员 " + member.getName() + " 已被冻结，请先解冻或联系管理员。");
    }

    Station station = stationMapper.findById(reservation.getStationId());
    if (station == null) {
      throw new ApiException("预约机位不存在，无法开机。");
    }
    if (!station.getId().equals(reservation.getStationId())) {
      throw new ApiException("预约单与机位不匹配，请重新选择。");
    }
    if (!"reserved".equals(station.getStatus())) {
      throw new ApiException("机位 " + station.getStationNo() + " 当前状态为「"
          + stationStatusLabel(station.getStatus()) + "」，暂不可按预约开机。");
    }
    if (now.isBefore(reservation.getStartTime()) || now.isAfter(reservation.getEndTime())) {
      throw new ApiException("当前时间不在预约时段（"
          + formatTime(reservation.getStartTime()) + " ~ " + formatTime(reservation.getEndTime())
          + "）内。");
    }

    BigDecimal hourlyRate = station.getHourlyRate() == null ? BigDecimal.ZERO : station.getHourlyRate();
    List<DurationPackage> packages = packageMapper.findUsable(member.getId(), now);
    int packageMinutes = packages.stream().mapToInt(DurationPackage::getRemainingMinutes).sum();
    BigDecimal balance = member.getBalance() == null ? BigDecimal.ZERO : member.getBalance();

    // 首小时先扣时长包；不足部分按机位时薪折算成金额，由余额兜底。
    int packageMinutesUsed = Math.min(FIRST_HOUR_MINUTES, packageMinutes);
    int minutesToPay = FIRST_HOUR_MINUTES - packageMinutesUsed;
    BigDecimal moneyNeeded = minutesToPay == 0
        ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        : hourlyRate.multiply(BigDecimal.valueOf(minutesToPay))
            .divide(BigDecimal.valueOf(FIRST_HOUR_MINUTES), 2, RoundingMode.HALF_UP);

    if (balance.compareTo(moneyNeeded) < 0) {
      // 余额不足：不扣时长包、不动余额、不改预约和机位状态，返回差额给前台。
      BigDecimal shortage = moneyNeeded.subtract(balance).setScale(2, RoundingMode.HALF_UP);
      return CheckInResult.insufficientFunds(member.getName(), station.getStationNo(),
          hourlyRate.setScale(2, RoundingMode.HALF_UP), packageMinutes,
          balance.setScale(2, RoundingMode.HALF_UP), shortage);
    }

    // 1) 扣时长包（临近到期优先）
    int remainingToDeduct = packageMinutesUsed;
    for (DurationPackage pkg : packages) {
      if (remainingToDeduct <= 0) {
        break;
      }
      int deduct = Math.min(remainingToDeduct, pkg.getRemainingMinutes());
      int updated = packageMapper.deductMinutes(pkg.getId(), deduct);
      if (updated == 0) {
        throw new ApiException("时长包「" + pkg.getName() + "」扣减失败，请重试。");
      }
      remainingToDeduct -= deduct;
    }

    // 2) 余额兜底扣款（条件更新，防止透支）
    BigDecimal balanceUsed = moneyNeeded;
    if (balanceUsed.compareTo(BigDecimal.ZERO) > 0
        && memberMapper.deductBalance(member.getId(), balanceUsed) == 0) {
      throw new ApiException("会员余额已变化，扣减失败，本次开机未生效，请刷新后重试。");
    }

    // 3) 写入上机记录
    Session session = new Session();
    session.setSessionNo(generateSessionNo());
    session.setReservationId(reservation.getId());
    session.setMemberId(member.getId());
    session.setStationId(station.getId());
    session.setStartedAt(now);
    session.setPackageMinutesUsed(packageMinutesUsed);
    session.setBalanceAmountUsed(balanceUsed);
    session.setFirstHourCharge(hourlyRate.setScale(2, RoundingMode.HALF_UP));
    session.setStatus("active");
    sessionMapper.insert(session);

    // 4) 机位切换为使用中（条件更新，防并发占用）
    if (stationMapper.updateStatus(station.getId(), "reserved", STATION_IN_USE) == 0) {
      throw new ApiException("机位 " + station.getStationNo() + " 状态已变化，开机失败，请刷新后重试。");
    }

    // 5) 预约单标记为已到店
    if (reservationMapper.markCheckedIn(reservation.getId(), RESERVATION_CHECKED_IN, now) == 0) {
      throw new ApiException("预约单 " + reservation.getReservationNo() + " 状态已变化，开机失败，请刷新后重试。");
    }

    // 6) 写入扣费明细，供运营台查看
    if (packageMinutesUsed > 0) {
      SessionCharge packageCharge = new SessionCharge();
      packageCharge.setSessionId(session.getId());
      packageCharge.setMemberId(member.getId());
      packageCharge.setChargeType("package");
      packageCharge.setMinutes(packageMinutesUsed);
      packageCharge.setAmount(BigDecimal.ZERO.setScale(2));
      packageCharge.setDetail("时长包抵扣首小时 " + packageMinutesUsed + " 分钟");
      chargeMapper.insert(packageCharge);
    }
    if (balanceUsed.compareTo(BigDecimal.ZERO) > 0) {
      SessionCharge balanceCharge = new SessionCharge();
      balanceCharge.setSessionId(session.getId());
      balanceCharge.setMemberId(member.getId());
      balanceCharge.setChargeType("balance");
      balanceCharge.setMinutes(minutesToPay);
      balanceCharge.setAmount(balanceUsed);
      balanceCharge.setDetail("余额兜底支付首小时剩余 " + minutesToPay + " 分钟");
      chargeMapper.insert(balanceCharge);
    }

    List<ChargeView> charges = sessionMapper.findChargesBySession(session.getId());
    String message;
    if (packageMinutesUsed == FIRST_HOUR_MINUTES) {
      message = "开机成功：时长包抵扣首小时 60 分钟，机位 " + station.getStationNo()
          + " 已切换为使用中。";
    } else if (packageMinutesUsed == 0) {
      message = "开机成功：余额支付首小时 " + balanceUsed + " 元，机位 "
          + station.getStationNo() + " 已切换为使用中。";
    } else {
      message = "开机成功：时长包抵扣 " + packageMinutesUsed + " 分钟，余额兜底 "
          + balanceUsed + " 元，机位 " + station.getStationNo() + " 已切换为使用中。";
    }
    return new CheckInResult(true, "OK", message, session.getId(), session.getSessionNo(),
        member.getName(), station.getStationNo(), now,
        hourlyRate.setScale(2, RoundingMode.HALF_UP), packageMinutesUsed, balanceUsed,
        BigDecimal.ZERO.setScale(2), charges);
  }

  private String generateSessionNo() {
    return "S" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        + String.format("%03d", ThreadLocalRandom.current().nextInt(1000));
  }

  private String formatTime(LocalDateTime time) {
    return time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
  }

  private String stationStatusLabel(String status) {
    return switch (status) {
      case "idle" -> "空闲";
      case "in_use" -> "使用中";
      case "reserved" -> "预约";
      case "fault" -> "故障";
      default -> status;
    };
  }

  private String reservationStatusLabel(String status) {
    return switch (status) {
      case "reserved" -> "已预约";
      case "checked_in" -> "已到店";
      case "completed" -> "已完成";
      case "cancelled" -> "已取消";
      default -> status;
    };
  }
}
