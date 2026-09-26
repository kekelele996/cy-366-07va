package com.generated.ldesportsbar.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import com.generated.ldesportsbar.model.SessionCharge;

@Mapper
public interface SessionChargeMapper {
  @Insert("""
      INSERT INTO session_charges (session_id, member_id, charge_type, minutes, amount, detail)
      VALUES (#{sessionId}, #{memberId}, #{chargeType}, #{minutes}, #{amount}, #{detail})
      """)
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(SessionCharge charge);
}
