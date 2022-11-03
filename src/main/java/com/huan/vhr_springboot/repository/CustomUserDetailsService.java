package com.huan.vhr_springboot.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.huan.vhr_springboot.entity.LoginUser;
import com.huan.vhr_springboot.mapper.LoginUserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Resource
    LoginUserMapper loginUsermapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<LoginUser> loginWrapper = new LambdaQueryWrapper<>();
        loginWrapper.select(LoginUser::getUid,LoginUser::getUname,LoginUser::getPhone,
                            LoginUser::getPassword,LoginUser::getEmail,LoginUser::getStatus,LoginUser::getImage);
        loginWrapper.eq(StringUtils.isNotBlank(username),LoginUser::getUname,username);
        LoginUser loginUser = loginUsermapper.selectOne(loginWrapper);
        if(loginUser.toString() == null) {
            throw new UsernameNotFoundException(username + "不存在！！");
        }
        return new CustomUserDetails(loginUser);
    }
}
