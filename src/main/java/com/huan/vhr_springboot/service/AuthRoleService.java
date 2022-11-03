package com.huan.vhr_springboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.entity.LoginAuthority;

public interface AuthRoleService {
    IPage<LoginAuthority> selectAllAuthRole(Long pageNo);
    Integer changeStatus(Long id,Integer status,Integer page);
    boolean selectHasUrlByLikeUrlAndAname(String aname,String url);
    Integer authorityadd(LoginAuthority loginAuthority);
}
