package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Personnel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface PersonnelMapper extends BaseMapper<Personnel> {
    List<Personnel> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize,@Param("hid") Long hid);

    Personnel selectBypId(@Param("pid") Long pid);

    @Select("select COUNT(*) as total from vhr_personnel where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_personnel where is_deleted=0 and house_id=#{hid}")
    Integer countTotalByHid(@Param("hid") Long hid);

    @Select("select COUNT(*) as total from vhr_personnel where is_deleted=0 and type=2")
    Integer countTotalTenant();
}
