package com.generated.ldesportsbar.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.generated.ldesportsbar.domain.ChargeRecord;
import com.generated.ldesportsbar.domain.Member;
import com.generated.ldesportsbar.domain.Reservation;
import com.generated.ldesportsbar.domain.Seat;
import com.generated.ldesportsbar.domain.TimePackage;
import com.generated.ldesportsbar.domain.UsageSession;
import com.generated.ldesportsbar.exception.ApiException;
import com.generated.ldesportsbar.mapper.ChargeRecordMapper;
import com.generated.ldesportsbar.mapper.MemberMapper;
import com.generated.ldesportsbar.mapper.ReservationMapper;
import com.generated.ldesportsbar.mapper.SeatMapper;
import com.generated.ldesportsbar.mapper.TimePackageMapper;
import com.generated.ldesportsbar.mapper.UsageSessionMapper;
import com.generated.ldesportsbar.web.ChargeDetail;
import com.generated.ldesportsbar.web.CheckinResult;

@Service
public class CheckinService {

  /** 到店先扣首小时（60 分钟）。 */
  static final int FIRST_HOUR_MINUTES = 60;
  /** 到店核验宽限窗口（分钟），预约开始前 120 分钟到结束后 60 分钟可开机。 */
  private static final int EARLY_WINDOW_MINUTES = 120;
  private static final int LATE_WINDOW_MINUTES = 60;

  private final ReservationMapper reservationMapper;
  private final SeatMapper seatMapper;
  private final MemberMapper memberMapper;
  private final TimePackageMapper timePackageMapper;
  private final UsageSessionMapper usageSessionMapper;
  private final ChargeRecordMapper chargeRecordMapper;

  public CheckinService(ReservationMapper reservationMapper,
                        SeatMapper seatMapper,
                        MemberMapper memberMapper,
                        TimePackageMapper timePackageMapper,
                        UsageSessionMapper usageSessionMapper,
                        ChargeRecordMapper chargeRecordMapper) {
    this.reservationMapper = reservationMapper;
    this.seatMapper = seatMapper;
    this.memberMapper = memberMapper;
    this.timePackageMapper = timePackageMapper;
    this.usageSessionMapper = usageSessionMapper;
    this.chargeRecordMapper = chargeRecordMapper;
  }

  /**
   * 到店开机主流程：核验会员与机位 -> 按时长包优先、余额兜底预扣首小时 ->
   * 写上机记录与扣费 -> 预约置已到店、机位切使用中。
   * 任一步失败都抛出 ApiException，事务回滚；余额不足返回 rejected 结果，预约与机位保持原状。
   */
  @Transactional
  public CheckinResult checkin(Long reservationId) {
    LocalDateTime now = LocalDateTime.now();

    Reservation reservation = reservationMapper.findById(reservationId);
    if (reservation == null) {
      throw new ApiException("预约单不存在，请确认后重试。");
    }
    if ("CHECKED_IN".equals(reservation.getStatus())) {
      throw new ApiException("该预约已到店开机，请勿重复操作。");
    }
    if ("CANCELLED".equals(reservation.getStatus())) {
      throw new ApiException("该预约已取消，无法开机。");
    }

    Member member = memberMapper.findById(reservation.getMemberId());
    if (member == null) {
      throw new ApiException("会员信息不存在，无法核验会员身份。");
    }

    Seat seat = seatMapper.findById(reservation.getSeatId());
    if (seat == null) {
      throw new ApiException("预约机位不存在，请联系值班经理。");
    }
    if ("FAULT".equals(seat.getStatus())) {
      throw new ApiException("机位 " + seat.getSeatNo() + " 当前为故障状态，暂不可开机，请先换机位。");
    }
    if (!"RESERVED".equals(seat.getStatus())) {
      throw new ApiException("机位 " + seat.getSeatNo() + " 当前为" + seatStatusLabel(seat.getStatus())
          + "状态，与预约不一致，请先人工核对机位。");
    }

    if (now.isBefore(reservation.getStartTime().minusMinutes(EARLY_WINDOW_MINUTES))) {
      throw new ApiException("尚未到开机时间（预约 " + formatTime(reservation.getStartTime()) + "），请稍后再试。");
    }
    if (now.isAfter(reservation.getEndTime().plusMinutes(LATE_WINDOW_MINUTES))) {
      throw new ApiException("已超过预约结束时间（" + formatTime(reservation.getEndTime()) + "），请重新预约。");
    }

    BigDecimal hourlyRate = seat.getHourlyRate().setScale(2, RoundingMode.HALF_UP);
    BigDecimal balanceBefore = member.getBalance() == null
        ? BigDecimal.ZERO.setScale(2)
        : member.getBalance().setScale(2, RoundingMode.HALF_UP);
    List<TimePackage> packages = timePackageMapper.findUsable(member.getId(), now);

    // 1) 时长包优先：按到期时间顺序消费
    int remainingNeed = FIRST_HOUR_MINUTES;
    int packageMinutesUsed = 0;
    BigDecimal packageCovered = BigDecimal.ZERO.setScale(2);
    List<ChargePlan> packagePlans = new ArrayList<>();
    for (TimePackage pkg : packages) {
      if (remainingNeed == 0) {
        break;
      }
      int take = Math.min(pkg.getRemainingMinutes(), remainingNeed);
      BigDecimal amount = hourlyRate.multiply(BigDecimal.valueOf(take))
          .divide(BigDecimal.valueOf(FIRST_HOUR_MINUTES), 2, RoundingMode.HALF_UP);
      packagePlans.add(new ChargePlan(pkg, take, amount));
      packageMinutesUsed += take;
      packageCovered = packageCovered.add(amount);
      remainingNeed -= take;
    }

    // 2) 余额兜底
    BigDecimal balanceNeeded = BigDecimal.ZERO.setScale(2);
    if (remainingNeed > 0) {
      balanceNeeded = hourlyRate.multiply(BigDecimal.valueOf(remainingNeed))
          .divide(BigDecimal.valueOf(FIRST_HOUR_MINUTES), 2, RoundingMode.HALF_UP);
    }

    // 3) 余额不足：保留预约和原机位状态，返回差额提示
    if (balanceNeeded.compareTo(balanceBefore) > 0) {
      BigDecimal shortfall = balanceNeeded.subtract(balanceBefore).setScale(2, RoundingMode.HALF_UP);
      int packageRemaining = timePackageMapper.sumUsableMinutes(member.getId(), now);
      List<ChargeDetail> wouldBe = new ArrayList<>();
      for (ChargePlan plan : packagePlans) {
        wouldBe.add(new ChargeDetail("PACKAGE", plan.minutes(), plan.amount()));
      }
      if (balanceNeeded.compareTo(BigDecimal.ZERO) > 0) {
        wouldBe.add(new ChargeDetail("BALANCE", remainingNeed, balanceNeeded));
      }
      String message = String.format(
          "会员 %s 余额不足：首小时费用 %.2f 元，时长包可抵扣 %d 分钟（%.2f 元），"
              + "还需余额 %.2f 元，当前余额 %.2f 元，差额 %.2f 元。请先为会员充值后再开机。",
          member.getName(), hourlyRate, packageMinutesUsed, packageCovered,
          balanceNeeded, balanceBefore, shortfall);
      return CheckinResult.rejected(
          message,
          reservation.getId(),
          reservation.getReservationNo(),
          member.getId(),
          member.getName(),
          seat.getId(),
          seat.getSeatNo(),
          hourlyRate,
          packageMinutesUsed,
          balanceBefore,
          shortfall,
          packageRemaining,
          wouldBe
      );
    }

    // 4) 原子地把机位从 RESERVED 切到 IN_USE，抢占到机位后才真正扣费
    int changed = seatMapper.compareAndSetStatus(seat.getId(), "RESERVED", "IN_USE");
    if (changed == 0) {
      throw new ApiException("机位 " + seat.getSeatNo() + " 状态刚发生变化，已为您保留预约，请刷新后重试。");
    }

    LocalDateTime startTime = now;
    LocalDateTime planEndTime = now.plusMinutes(FIRST_HOUR_MINUTES);

    UsageSession session = new UsageSession();
    session.setMemberId(member.getId());
    session.setSeatId(seat.getId());
    session.setReservationId(reservation.getId());
    session.setStartTime(startTime);
    session.setPlanEndTime(planEndTime);
    session.setStatus("IN_USE");
    session.setChargedMinutes(FIRST_HOUR_MINUTES);
    session.setChargeSummary(buildChargeSummary(packageMinutesUsed, packageCovered,
        remainingNeed > 0 ? remainingNeed : 0, balanceNeeded));
    usageSessionMapper.insert(session);

    List<ChargeDetail> charges = new ArrayList<>();
    for (ChargePlan plan : packagePlans) {
      int rows = timePackageMapper.deductMinutes(plan.pkg().getId(), plan.minutes());
      if (rows == 0) {
        throw new ApiException("时长包扣减失败，已为您保留预约和机位，请重试。");
      }
      ChargeRecord record = new ChargeRecord();
      record.setSessionId(session.getId());
      record.setMemberId(member.getId());
      record.setChargeType("PACKAGE");
      record.setMinutes(plan.minutes());
      record.setAmount(plan.amount());
      chargeRecordMapper.insert(record);
      charges.add(new ChargeDetail("PACKAGE", plan.minutes(), plan.amount()));
    }

    BigDecimal balanceUsed = BigDecimal.ZERO.setScale(2);
    if (balanceNeeded.compareTo(BigDecimal.ZERO) > 0) {
      int rows = memberMapper.deductBalance(member.getId(), balanceNeeded);
      if (rows == 0) {
        throw new ApiException("余额扣减失败（可能已被其他消费占用），已为您保留预约，请重试。");
      }
      ChargeRecord record = new ChargeRecord();
      record.setSessionId(session.getId());
      record.setMemberId(member.getId());
      record.setChargeType("BALANCE");
      record.setMinutes(remainingNeed);
      record.setAmount(balanceNeeded);
      chargeRecordMapper.insert(record);
      charges.add(new ChargeDetail("BALANCE", remainingNeed, balanceNeeded));
      balanceUsed = balanceNeeded;
    }

    int reservationRows = reservationMapper.updateStatus(reservation.getId(), "CHECKED_IN");
    if (reservationRows == 0) {
      throw new ApiException("预约单状态更新失败，请重试。");
    }

    Member refreshedMember = memberMapper.findById(member.getId());
    int remainingPackageMinutes = timePackageMapper.sumUsableMinutes(member.getId(), LocalDateTime.now());
    String message = String.format("开机成功：机位 %s 已切为使用中，已扣首小时费用 %.2f 元（时长包 %d 分钟、余额 %.2f 元）。",
        seat.getSeatNo(), hourlyRate, packageMinutesUsed, balanceUsed);

    return new CheckinResult(
        true,
        message,
        reservation.getId(),
        reservation.getReservationNo(),
        session.getId(),
        member.getId(),
        member.getName(),
        seat.getId(),
        seat.getSeatNo(),
        startTime,
        planEndTime,
        FIRST_HOUR_MINUTES,
        hourlyRate,
        packageMinutesUsed,
        balanceUsed,
        BigDecimal.ZERO.setScale(2),
        refreshedMember.getBalance().setScale(2, RoundingMode.HALF_UP),
        remainingPackageMinutes,
        "IN_USE",
        charges
    );
  }

  private String buildChargeSummary(int packageMinutes, BigDecimal packageCovered,
                                    int balanceMinutes, BigDecimal balanceAmount) {
    List<String> parts = new ArrayList<>();
    if (packageMinutes > 0) {
      parts.add(String.format("时长包 %d 分钟（%.2f 元）", packageMinutes, packageCovered));
    }
    if (balanceMinutes > 0) {
      parts.add(String.format("余额 %d 分钟（%.2f 元）", balanceMinutes, balanceAmount));
    }
    return parts.isEmpty() ? "首小时免费" : String.join("、", parts);
  }

  private String seatStatusLabel(String status) {
    return switch (status) {
      case "IDLE" -> "空闲";
      case "IN_USE" -> "使用中";
      case "RESERVED" -> "已预约";
      case "FAULT" -> "故障";
      default -> status;
    };
  }

  private String formatTime(LocalDateTime time) {
    return time.toString().replace('T', ' ');
  }

  private record ChargePlan(TimePackage pkg, int minutes, BigDecimal amount) {
  }
}
