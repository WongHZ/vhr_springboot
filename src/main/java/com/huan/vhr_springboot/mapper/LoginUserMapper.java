package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("login")
@Repository
public interface LoginUserMapper extends BaseMapper<LoginUser> {

    List<LoginUser> selectPage(@Param("pageNo") Long pageNo, @Param("pageSize") Long pageSize, @Param("rid") Long rid);

    @Select("select COUNT(*) as total from user")
    Integer countTotal();

    @Select("select COUNT(*) as total from user u " +
            "left join user_role ur on u.u_id = ur.user_id " +
            "left join role r on r.r_id = ur.role_id where r.r_id=#{rid}")
    Integer countTotalByRid(@Param("rid") Long rid);

    @Select("select * from user_role where user_id=#{userId}")
    List<LoginUserRole> findUserRolesByUserId(Long userId);

    LoginUser selectRole(@Param("uname") String uname);
}
