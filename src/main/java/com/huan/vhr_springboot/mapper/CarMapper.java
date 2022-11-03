package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Car;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface CarMapper extends BaseMapper<Car> {
    List<Car> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize,@Param("hid") Long hid);

    @Select("select COUNT(*) as total from vhr_car where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_car where is_deleted=0 and house_id=#{hid}")
    Integer countTotalByHid(@Param("hid") Long hid);

    Car selectDataById(@Param("cid") Long cid);
}
