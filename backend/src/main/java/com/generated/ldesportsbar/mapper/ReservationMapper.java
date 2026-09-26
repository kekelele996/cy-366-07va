package com.generated.ldesportsbar.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.domain.Reservation;
import com.generated.ldesportsbar.web.ReservationRow;

@Mapper
public interface ReservationMapper {
  @Select("SELECT id, reservation_no, member_id, seat_id, start_time, end_time, status, created_at "
      + "FROM reservations WHERE id = #{id}")
  Reservation findById(@Param("id") Long id);

  @Select("""
      <script>
      SELECT r.id, r.reservation_no, r.member_id, r.seat_id, r.start_time, r.end_time, r.status,
             m.member_no AS member_no, m.name AS member_name, m.balance AS member_balance,
             s.seat_no AS seat_no, s.area AS seat_area, s.seat_type AS seat_type,
             s.status AS seat_status, s.hourly_rate AS hourly_rate
      FROM reservations r
      JOIN members m ON m.id = r.member_id
      JOIN seats s ON s.id = r.seat_id
      <where>
        <if test="status != null">AND r.status = #{status}</if>
      </where>
      ORDER BY r.start_time ASC, r.id ASC
      </script>
      """)
  List<ReservationRow> findRows(@Param("status") String status);

  @Select("""
      SELECT r.id, r.reservation_no, r.member_id, r.seat_id, r.start_time, r.end_time, r.status,
             m.member_no AS member_no, m.name AS member_name, m.balance AS member_balance,
             s.seat_no AS seat_no, s.area AS seat_area, s.seat_type AS seat_type,
             s.status AS seat_status, s.hourly_rate AS hourly_rate
      FROM reservations r
      JOIN members m ON m.id = r.member_id
      JOIN seats s ON s.id = r.seat_id
      WHERE r.id = #{id}
      """)
  ReservationRow findRowById(@Param("id") Long id);

  @Update("UPDATE reservations SET status = #{status} WHERE id = #{id}")
  int updateStatus(@Param("id") Long id, @Param("status") String status);
}
