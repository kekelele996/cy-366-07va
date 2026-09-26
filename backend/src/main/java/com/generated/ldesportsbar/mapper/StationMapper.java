package com.generated.ldesportsbar.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.model.Station;

@Mapper
public interface StationMapper {
  @Select("SELECT * FROM stations WHERE id = #{id}")
  Station findById(@Param("id") Long id);

  @Select("SELECT * FROM stations ORDER BY station_no")
  List<Station> findAll();

  /**
   * 条件更新机位状态，防止并发下抢占已被占用的机位。
   */
  @Update("UPDATE stations SET status = #{toStatus} WHERE id = #{id} AND status = #{expectedStatus}")
  int updateStatus(@Param("id") Long id, @Param("expectedStatus") String expectedStatus,
      @Param("toStatus") String toStatus);
}
