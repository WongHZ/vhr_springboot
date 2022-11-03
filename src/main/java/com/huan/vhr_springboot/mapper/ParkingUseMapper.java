package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.ParkingUse;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface ParkingUseMapper extends BaseMapper<ParkingUse> {
    List<ParkingUse> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("pid") Long pid);

    @Select("select COUNT(*) as total from vhr_parking_use where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_parking_use where is_deleted=0 and parking_id=#{pid}")
    Integer countTotalByPid(@Param("pid") Long pid);

    ParkingUse selectDataById(@Param("id") Long id);
}
