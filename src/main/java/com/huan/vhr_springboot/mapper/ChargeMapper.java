package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Charge;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface ChargeMapper extends BaseMapper<Charge> {
    List<Charge> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("chid") Long chid);

    @Select("select COUNT(*) as total from vhr_charge where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_charge where is_deleted=0 and charge_id=#{chid}")
    Integer countTotalByChid(@Param("chid") Long chid);
}
