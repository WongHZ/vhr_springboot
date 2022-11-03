package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.LoginAuthority;
import com.huan.vhr_springboot.entity.LoginRoleAuthority;
import com.huan.vhr_springboot.entity.LoginUserRole;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@DS("login")
@Repository
public interface LoginUserRoleMapper extends BaseMapper<LoginUserRole> {

    @Select("select * from user_role where user_id=#{userId}")
    List<LoginUserRole> findUserRolesByUserId(Long userId);

    @Select("select * from role_authority where role_id=#{roleIds}")
    List<LoginRoleAuthority> findRoleAuthoritiesByRoleIdIn(Long roleIds);


    @Select("select * from authority where a_id=#{authorityIds}")
    List<LoginAuthority> findAllById(Long authorityIds);
}
