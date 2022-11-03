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
        //创建random对象给存储redis时设置随机存储时间
        /**
         * 通过用户 id 查询角色id
         * if redis 没有 roldId; then 从数据库查
         *  else redis 有roldId; then 从redis取
         *  redis 过期时间为一天+3分钟以内的随机值
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
            log.info("roleid从数据库拿");
        }else {
            roleId = (Long) redisTemplate.opsForHash().get(MANAGER + "uid","uid_" + uid);
            log.info("roleid从redis拿");
        }

        /**
         * 通过角色 id 查询对应的权限名字
         * if redis 没有 authorityName; then 从数据库查
         *  else redis 有authorityName; then 从redis取
         *  redis 过期时间为一天+3分钟以内的随机值
         */
        String authorityName;
        if(! redisTemplate.opsForHash().hasKey(MANAGER + "aname","uid_" + uid)){
            LambdaQueryWrapper<LoginRoleAuthority> roleIdwrapper = new LambdaQueryWrapper<>();
            roleIdwrapper.eq(LoginRoleAuthority::getRid,roleId);
            LoginRoleAuthority role_authorities = loginRoleAuthorityMapper.selectOne(roleIdwrapper);
            authorityName = role_authorities.getAuthname();
            redisTemplate.opsForHash().put(MANAGER + "aname","uid_" + uid,authorityName);
            redisTemplate.expire(MANAGER + "aname",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("authName从数据库拿");
        }else {
            authorityName = (String) redisTemplate.opsForHash().get(MANAGER + "aname","uid_" + uid);
            log.info("authName从redis拿");
        }

        List<LoginAuthority> authorities;
        if(! redisTemplate.opsForHash().hasKey("authrole_url",authorityName)){
            //通过权限 名字 查询权限表
            LambdaQueryWrapper<LoginAuthority> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LoginAuthority::getAname,authorityName).eq(LoginAuthority::getStatus,1);
            authorities = loginAuthorityMapper.selectList(wrapper);
            redisTemplate.opsForHash().put("authrole_url",authorityName,authorities);
            redisTemplate.expire("authority",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("权限表从数据库拿");
        }else {
            authorities = (List<LoginAuthority>) redisTemplate.opsForHash().get("authrole_url",authorityName);
            log.info("权限表从redis拿");
        }


        //获取请求路径
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
        throw new AccessDeniedException("您的账号暂时无法访问此页面");
    }
}
