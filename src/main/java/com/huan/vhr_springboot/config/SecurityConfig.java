package com.huan.vhr_springboot.config;

import com.huan.vhr_springboot.filter.SecurityFilter;
import com.huan.vhr_springboot.repository.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.annotation.Resource;

import static org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Resource
    private SecurityFilter securityFilter;
    @Resource
    private RequestMatcher[] requestMatchers;

    @Resource
    private CustomAuthorizationManager customAuthorizationManager;

    @Resource
    private CustomUserDetailsService customUserDetailsService;

     /*
     * @param httpSecurity
     * @return 通过build创建securityFilter链
     * @throws Exception
     *
     * csrf().disable() 关闭防火墙
     * formLogin().disable() 关闭默认登录
     * requestMatchers(requestMatchers) 自定义放行规则
     * anyRequest().access() 通过此类验证放行
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()
                .csrf().disable()
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .disable()
                        )
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER))
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers(requestMatchers).permitAll()
                    .anyRequest().access(customAuthorizationManager)
                )
                .formLogin().disable()
                .logout().logoutUrl("http://localhost:8080/logouts").and()
                .addFilterAfter(securityFilter, ExceptionTranslationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling()
                    .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("text/plain;charset=UTF-8");
                            response.getWriter().write(authException.getMessage());
                })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.sendRedirect("http://localhost:8080/denied");
                       /* response.setContentType("text/plain;charset=UTF-8");
                        response.getWriter().write(accessDeniedException.getMessage());*/
                });
        return httpSecurity.build();
    }


     /* 创建密码加密
     * @return
      */

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


     /*
     定义 Spring Security 的过滤器如何执行身份验证的 API
     * DaoAuthenticationProvider 最常用是用此类创建provider
     * @return
      */

    @Bean
    public AuthenticationManager authenticationManager(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(customUserDetailsService);
        return new ProviderManager(provider);
    }
}
