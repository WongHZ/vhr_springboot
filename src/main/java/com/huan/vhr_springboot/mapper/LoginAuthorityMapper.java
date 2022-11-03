package com.huan.vhr_springboot.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huan.vhr_springboot.entity.LoginAuthority;
import org.springframework.stereotype.Repository;

@DS("login")
@Repository
public interface LoginAuthorityMapper extends BaseMapper<LoginAuthority> {
}
