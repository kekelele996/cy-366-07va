package com.generated.ldesportsbar.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.generated.ldesportsbar.domain.UsageSession;
import com.generated.ldesportsbar.web.SessionRow;

@Mapper
public interface UsageSessionMapper {
  @Insert("""
      INSERT INTO usage_sessions
        (member_id, seat_id, reservation_id, start_time, plan_end_time, status, charged_minutes, charge_summary)
      VALUES
        (#{memberId}, #{seatId}, #{reservationId}, #{startTime}, #{planEndTime},
         #{status}, #{chargedMinutes}, #{chargeSummary})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(UsageSession session);

  @Select("""
      SELECT us.id, us.member_id, us.seat_id, us.reservation_id, us.start_time, us.plan_end_time,
             us.end_time, us.status, us.charged_minutes, us.charge_summary,
             m.member_no AS member_no, m.name AS member_name,
             s.seat_no AS seat_no, s.area AS seat_area, s.seat_type AS seat_type,
             s.hourly_rate AS hourly_rate,
             r.reservation_no AS reservation_no
      FROM usage_sessions us
      JOIN members m ON m.id = us.member_id
      JOIN seats s ON s.id = us.seat_id
      LEFT JOIN reservations r ON r.id = us.reservation_id
      WHERE us.status = #{status}
      ORDER BY us.start_time DESC, us.id DESC
      """)
  List<SessionRow> findRowsByStatus(@Param("status") String status);

  @Select("""
      SELECT us.id, us.member_id, us.seat_id, us.reservation_id, us.start_time, us.plan_end_time,
             us.end_time, us.status, us.charged_minutes, us.charge_summary,
             m.member_no AS member_no, m.name AS member_name,
             s.seat_no AS seat_no, s.area AS seat_area, s.seat_type AS seat_type,
             s.hourly_rate AS hourly_rate,
             r.reservation_no AS reservation_no
      FROM usage_sessions us
      JOIN members m ON m.id = us.member_id
      JOIN seats s ON s.id = us.seat_id
      LEFT JOIN reservations r ON r.id = us.reservation_id
      ORDER BY us.created_at DESC, us.id DESC
      LIMIT #{limit}
      """)
  List<SessionRow> findRecentRows(@Param("limit") int limit);
}
