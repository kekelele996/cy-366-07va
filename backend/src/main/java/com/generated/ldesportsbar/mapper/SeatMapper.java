package com.generated.ldesportsbar.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.domain.Seat;

@Mapper
public interface SeatMapper {
  @Select("SELECT id, seat_no, area, zone, seat_type, status, hourly_rate, created_at FROM seats WHERE id = #{id}")
  Seat findById(@Param("id") Long id);

  @Select("SELECT id, seat_no, area, zone, seat_type, status, hourly_rate, created_at FROM seats ORDER BY zone, seat_no")
  List<Seat> findAll();

  @Select("""
      SELECT id, seat_no, area, zone, seat_type, status, hourly_rate, created_at
      FROM seats WHERE status = #{status} ORDER BY zone, seat_no
      """)
  List<Seat> findByStatus(@Param("status") String status);

  /** 仅当机位仍是期望状态时才切换，防止并发/他人抢占。 */
  @Update("UPDATE seats SET status = #{toStatus} WHERE id = #{id} AND status = #{expectedStatus}")
  int compareAndSetStatus(@Param("id") Long id,
                          @Param("expectedStatus") String expectedStatus,
                          @Param("toStatus") String toStatus);
}
