package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Community;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@DS("master")
@Repository
public interface CommunityMapper extends BaseMapper<Community> {
    @Select("SELECT IFNULL(sum(total_buildings),0) as totalBuildings FROM vhr_community")
    public BigDecimal totalBuildings();

    @Select("SELECT IFNULL(sum(total_households),0) as totalBuildings FROM vhr_community")
    public BigDecimal totalHouseholds();
}
