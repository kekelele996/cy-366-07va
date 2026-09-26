package com.generated.ldesportsbar.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.model.Reservation;
import com.generated.ldesportsbar.model.dto.ReservationView;

@Mapper
public interface ReservationMapper {
  @Select("SELECT * FROM reservations WHERE id = #{id}")
  Reservation findById(@Param("id") Long id);

  @Select("""
      SELECT r.id AS reservation_id, r.reservation_no,
             m.id AS member_id, m.member_no, m.name AS member_name, m.phone AS member_phone,
             m.balance,
             (SELECT COALESCE(SUM(dp.remaining_minutes), 0)
                FROM duration_packages dp
               WHERE dp.member_id = m.id
                 AND dp.status = 'active'
                 AND dp.remaining_minutes > 0
                 AND (dp.valid_from IS NULL OR dp.valid_from <= CURRENT_TIMESTAMP)
                 AND (dp.valid_until IS NULL OR dp.valid_until >= CURRENT_TIMESTAMP)
             ) AS package_minutes,
             s.id AS station_id, s.station_no, s.zone_name, s.status AS station_status,
             s.hourly_rate, r.start_time, r.end_time, r.status
      FROM reservations r
      JOIN members m ON m.id = r.member_id
      JOIN stations s ON s.id = r.station_id
      WHERE r.status = 'reserved'
      ORDER BY r.start_time ASC
      """)
  List<ReservationView> findArrivals();

  @Update("UPDATE reservations SET status = #{status}, checked_in_at = #{checkedInAt} "
      + "WHERE id = #{id} AND status = 'reserved'")
  int markCheckedIn(@Param("id") Long id, @Param("status") String status,
      @Param("checkedInAt") LocalDateTime checkedInAt);
}
