package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.LoginRole;
import com.huan.vhr_springboot.entity.LoginUser;
import com.huan.vhr_springboot.entity.LoginUserRole;

import java.util.List;

public interface AdminService {
    List<LoginUser> selectAllLoginUser(Long page, Long rid);
    Integer total(Long uid);
    Integer pageNum(Integer total);
    Integer changeStatus(Long id,Integer status,Integer page);
    List<LoginRole> roleList();
    Integer adminadd(LoginUser loginUser);
    Long selectUidByPhone(Long phone);
    Integer userRoleAdd(LoginUserRole loginUserRole);
    Integer admindel(Long id,Integer page);
    Integer adminRoledel(Long id);
    String selectRole(String username);
    List<LoginUser> selectAllUser();
    Integer updateUser(LoginUser user);
    LoginUser selectDataByUidOrName(Long uid,String name);
    Integer updatePass(Long uid,String npass);
}
