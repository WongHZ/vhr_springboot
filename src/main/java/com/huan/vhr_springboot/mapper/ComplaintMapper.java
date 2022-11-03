package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.Complaint;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("master")
@Repository
public interface ComplaintMapper extends BaseMapper<Complaint> {
    List<Complaint> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("aname") String aname);

    @Select("select COUNT(*) as total from vhr_complaint where is_deleted=0")
    Integer countTotal();

    @Select("select COUNT(*) as total from vhr_complaint where is_deleted=0 and status=0")
    Integer countTotalNew();

    @Select("select COUNT(*) as total from vhr_complaint where is_deleted=0 and admin_name=#{aname}")
    Integer countTotalByAname(@Param("aname") String aname);
}
