package com.huan.vhr_springboot.config;

/*import com.huan.vhr_springboot.repository.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;*/

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huan.vhr_springboot.entity.LoginAuthority;
import com.huan.vhr_springboot.entity.LoginRoleAuthority;
import com.huan.vhr_springboot.entity.LoginUserRole;
import com.huan.vhr_springboot.mapper.LoginAuthorityMapper;
import com.huan.vhr_springboot.mapper.LoginRoleAuthorityMapper;
import com.huan.vhr_springboot.mapper.LoginUserMapper;
import com.huan.vhr_springboot.mapper.LoginUserRoleMapper;
import com.huan.vhr_springboot.repository.CustomUserDetails;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
@Slf4j
@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    private static final String MANAGER = "manager_";
    private static final Integer DAYTTL = 86400;
    @Resource
    private LoginAuthorityMapper loginAuthorityMapper;
    @Resource
    private LoginUserRoleMapper loginUserRoleMapper;
    @Resource
    private LoginRoleAuthorityMapper loginRoleAuthorityMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.get().getPrincipal();
        //??????random???????????????redis???????????????????????????
        /**
         * ???????????? id ????????????id
         * if redis ?????? roldId; then ???????????????
         *  else redis ???roldId; then ???redis???
         *  redis ?????????????????????+3????????????????????????
         */
        Long uid = customUserDetails.getUid();
        Long roleId;
        if(! redisTemplate.opsForHash().hasKey(MANAGER + "uid","uid_" + uid)){
            LambdaQueryWrapper<LoginUserRole> uIdwrapper = new LambdaQueryWrapper<>();
            uIdwrapper.eq(LoginUserRole::getUid,uid);
            LoginUserRole user_roles = loginUserRoleMapper.selectOne(uIdwrapper);
            roleId = user_roles.getRid();
            redisTemplate.opsForHash().put(MANAGER + "uid","uid_" + uid,roleId);
            redisTemplate.expire(MANAGER + "uid",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("roleid???????????????");
        }else {
            roleId = (Long) redisTemplate.opsForHash().get(MANAGER + "uid","uid_" + uid);
            log.info("roleid???redis???");
        }

        /**
         * ???????????? id ???????????????????????????
         * if redis ?????? authorityName; then ???????????????
         *  else redis ???authorityName; then ???redis???
         *  redis ?????????????????????+3????????????????????????
         */
        String authorityName;
        if(! redisTemplate.opsForHash().hasKey(MANAGER + "aname","uid_" + uid)){
            LambdaQueryWrapper<LoginRoleAuthority> roleIdwrapper = new LambdaQueryWrapper<>();
            roleIdwrapper.eq(LoginRoleAuthority::getRid,roleId);
            LoginRoleAuthority role_authorities = loginRoleAuthorityMapper.selectOne(roleIdwrapper);
            authorityName = role_authorities.getAuthname();
            redisTemplate.opsForHash().put(MANAGER + "aname","uid_" + uid,authorityName);
            redisTemplate.expire(MANAGER + "aname",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("authName???????????????");
        }else {
            authorityName = (String) redisTemplate.opsForHash().get(MANAGER + "aname","uid_" + uid);
            log.info("authName???redis???");
        }

        List<LoginAuthority> authorities;
        if(! redisTemplate.opsForHash().hasKey("authrole_url",authorityName)){
            //???????????? ?????? ???????????????
            LambdaQueryWrapper<LoginAuthority> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LoginAuthority::getAname,authorityName).eq(LoginAuthority::getStatus,1);
            authorities = loginAuthorityMapper.selectList(wrapper);
            redisTemplate.opsForHash().put("authrole_url",authorityName,authorities);
            redisTemplate.expire("authority",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("????????????????????????");
        }else {
            authorities = (List<LoginAuthority>) redisTemplate.opsForHash().get("authrole_url",authorityName);
            log.info("????????????redis???");
        }


        //??????????????????
        HttpServletRequest request = requestAuthorizationContext.getRequest();
        boolean hasPermission = false;
        for(LoginAuthority i : authorities){
            AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher(i.getUrl(),i.getMethod());
            if(antPathRequestMatcher.matches(request)){
                hasPermission = true;
                break;
            }
        }

        if(hasPermission){
            return new AuthorizationDecision(hasPermission);
        }
        throw new AccessDeniedException("???????????????????????????????????????");
    }
}
