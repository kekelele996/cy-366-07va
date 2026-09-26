package com.generated.ldesportsbar.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.generated.ldesportsbar.model.Session;
import com.generated.ldesportsbar.model.dto.ActiveStationView;
import com.generated.ldesportsbar.model.dto.ChargeView;

@Mapper
public interface SessionMapper {
  @Insert("""
      INSERT INTO sessions
        (session_no, reservation_id, member_id, station_id, started_at,
         package_minutes_used, balance_amount_used, first_hour_charge, status)
      VALUES
        (#{sessionNo}, #{reservationId}, #{memberId}, #{stationId}, #{startedAt},
         #{packageMinutesUsed}, #{balanceAmountUsed}, #{firstHourCharge}, #{status})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Session session);

  @Select("""
      SELECT se.id AS session_id, se.session_no,
             st.id AS station_id, st.station_no, st.zone_name,
             m.id AS member_id, m.member_no, m.name AS member_name,
             r.reservation_no, se.started_at, se.first_hour_charge,
             se.package_minutes_used, se.balance_amount_used
      FROM sessions se
      JOIN stations st ON st.id = se.station_id
      JOIN members m ON m.id = se.member_id
      LEFT JOIN reservations r ON r.id = se.reservation_id
      WHERE se.status = 'active'
      ORDER BY se.started_at ASC
      """)
  List<ActiveStationView> findActiveStations();

  @Select("""
      SELECT c.id, c.session_id, c.member_id, m.name AS member_name,
             st.station_no, c.charge_type, c.minutes, c.amount, c.detail, c.created_at
      FROM session_charges c
      JOIN members m ON m.id = c.member_id
      JOIN sessions se ON se.id = c.session_id
      JOIN stations st ON st.id = se.station_id
      ORDER BY c.created_at DESC
      LIMIT #{limit}
      """)
  List<ChargeView> findRecentCharges(@Param("limit") int limit);

  @Select("""
      <script>
      SELECT c.id, c.session_id, c.member_id, m.name AS member_name,
             st.station_no, c.charge_type, c.minutes, c.amount, c.detail, c.created_at
      FROM session_charges c
      JOIN members m ON m.id = c.member_id
      JOIN sessions se ON se.id = c.session_id
      JOIN stations st ON st.id = se.station_id
      <where>
        <if test="sessionId != null">AND c.session_id = #{sessionId}</if>
      </where>
      ORDER BY c.id ASC
      </script>
      """)
  List<ChargeView> findChargesBySession(@Param("sessionId") Long sessionId);
}
