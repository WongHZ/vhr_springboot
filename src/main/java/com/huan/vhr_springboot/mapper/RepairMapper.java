package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Repair;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface RepairMapper extends BaseMapper<Repair> {
    List<Repair> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("cname") String cname);

    @Select("select COUNT(*) as total from vhr_repair where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_repair where is_deleted=0 and admin_name=#{cname}")
    Integer countTotalByCname(@Param("cname") String cname);
}
