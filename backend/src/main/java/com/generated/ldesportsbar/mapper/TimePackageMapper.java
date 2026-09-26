package com.generated.ldesportsbar.mapper;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.domain.TimePackage;

@Mapper
public interface TimePackageMapper {
  /** 时长包按到期时间早的优先消费，未过期且仍有剩余分钟。 */
  @Select("""
      SELECT id, member_id, package_name, total_minutes, remaining_minutes, expire_at, created_at
      FROM time_packages
      WHERE member_id = #{memberId}
        AND remaining_minutes > 0
        AND (expire_at IS NULL OR expire_at >= #{now})
      ORDER BY expire_at ASC, id ASC
      """)
  List<TimePackage> findUsable(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);

  @Update("UPDATE time_packages SET remaining_minutes = remaining_minutes - #{minutes} "
      + "WHERE id = #{id} AND remaining_minutes >= #{minutes}")
  int deductMinutes(@Param("id") Long id, @Param("minutes") int minutes);

  /** 会员当前可用（未过期且有余额）时长包的剩余分钟合计。 */
  @Select("""
      SELECT COALESCE(SUM(remaining_minutes), 0)
      FROM time_packages
      WHERE member_id = #{memberId}
        AND remaining_minutes > 0
        AND (expire_at IS NULL OR expire_at >= #{now})
      """)
  int sumUsableMinutes(@Param("memberId") Long memberId, @Param("now") LocalDateTime now);
}
