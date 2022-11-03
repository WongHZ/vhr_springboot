package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Building;
import org.springframework.stereotype.Repository;

@DS("master")
@Repository
public interface BuildingMapper extends BaseMapper<Building> {
}
