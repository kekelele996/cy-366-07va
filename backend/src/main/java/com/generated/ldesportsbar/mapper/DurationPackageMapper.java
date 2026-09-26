package com.generated.ldesportsbar.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.model.DurationPackage;

@Mapper
public interface DurationPackageMapper {
  /**
   * 查询会员当前可用的时长包（状态有效、在有效期内、仍有剩余分钟），
   * 即将到期的优先扣减，其次按最早购买顺序。
   */
  @Select("""
      SELECT * FROM duration_packages
      WHERE member_id = #{memberId}
        AND status = 'active'
        AND remaining_minutes > 0
        AND (valid_from IS NULL OR valid_from <= #{now})
        AND (valid_until IS NULL OR valid_until >= #{now})
      ORDER BY CASE WHEN valid_until IS NULL THEN 1 ELSE 0 END, valid_until ASC, id ASC
      """)
  List<DurationPackage> findUsable(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

  @Update("UPDATE duration_packages SET remaining_minutes = remaining_minutes - #{minutes} "
      + "WHERE id = #{id} AND remaining_minutes >= #{minutes}")
  int deductMinutes(@Param("id") Long id, @Param("minutes") int minutes);
}
