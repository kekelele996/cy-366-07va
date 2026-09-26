package com.generated.ldesportsbar.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.generated.ldesportsbar.domain.ChargeRecord;

@Mapper
public interface ChargeRecordMapper {
  @Insert("""
      INSERT INTO charge_records (session_id, member_id, charge_type, minutes, amount)
      VALUES (#{sessionId}, #{memberId}, #{chargeType}, #{minutes}, #{amount})
      """)
  int insert(ChargeRecord record);

  @Select("SELECT id, session_id, member_id, charge_type, minutes, amount, created_at "
      + "FROM charge_records WHERE session_id = #{sessionId} ORDER BY id ASC")
  List<ChargeRecord> findBySessionId(@Param("sessionId") Long sessionId);
}
