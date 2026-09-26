package com.generated.ldesportsbar.mapper;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import com.generated.ldesportsbar.model.Member;

@Mapper
public interface MemberMapper {
  @Select("SELECT * FROM members WHERE id = #{id}")
  Member findById(@Param("id") Long id);

  @Update("UPDATE members SET balance = balance - #{amount} WHERE id = #{id} AND balance >= #{amount}")
  int deductBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
