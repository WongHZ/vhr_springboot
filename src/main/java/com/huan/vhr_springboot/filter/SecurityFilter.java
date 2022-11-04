package com.huan.vhr_springboot.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.huan.vhr_springboot.repository.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Slf4j
@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private RequestMatcher[] notAuthenticationMatchers;

    public boolean requireAuthentication(HttpServletRequest request) {
        if (notAuthenticationMatchers == null || notAuthenticationMatchers.length == 0) {
            return true;
        }
        for (RequestMatcher requestMatcher : notAuthenticationMatchers) {
            if (requestMatcher.matches(request)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!requireAuthentication(request)){
            filterChain.doFilter(request,response);
            return;
        }
        //String header = request.getHeader("Authorization");
        String head = request.getHeader("Cookie");
        if(! head.contains("Authorization=")){
            throw new BadCredentialsException("令牌为空！请重试！");
        }
        String header = head.substring(head.indexOf("Authorization="));
        String header2 = header.replace("Authorization=","");
        if(!StrUtil.startWith(header2,"Bearer")){
            throw new BadCredentialsException("令牌类型不正确！");
        }
        String token = header2.substring(7);
        log.info("token为：" + token);
        JWTSigner jwtSigner = JWTSignerUtil.hs512("vhr_login".getBytes(StandardCharsets.UTF_8));
        if(!JWTUtil.verify(token,jwtSigner)){
            throw new BadCredentialsException("令牌不正确");
        }
        JWT jwt = JWTUtil.parseToken(token);
        String username = (String) jwt.getPayload().getClaim("username");
        CustomUserDetails userDetails = (CustomUserDetails) redisTemplate.opsForHash().get("login_user",username);
        if(userDetails == null){
            throw new UsernameNotFoundException("暂无用户信息，请重试");
        }
        SecurityContextHolder.getContext()
                .setAuthentication(UsernamePasswordAuthenticationToken
                        .authenticated(userDetails,null,null));
        filterChain.doFilter(request,response);
    }
}
