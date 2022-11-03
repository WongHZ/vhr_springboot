package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Paylist;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface PaylistMapper extends BaseMapper<Paylist> {
    List<Paylist> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("aname") String aname);

    @Select("select COUNT(*) as total from vhr_paylist where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_paylist where is_deleted=0 and admin_name=#{aname}")
    Integer countTotalByAname(@Param("aname") String aname);
}
