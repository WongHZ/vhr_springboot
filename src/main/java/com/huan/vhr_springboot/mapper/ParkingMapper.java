package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Parking;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface ParkingMapper extends BaseMapper<Parking> {
    List<Parking> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("cid") Long cid);

    @Select("select COUNT(*) as total from vhr_parking where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_parking where is_deleted=0 and community_id=#{cid}")
    Integer countTotalByCid(@Param("cid") Long cid);
}
