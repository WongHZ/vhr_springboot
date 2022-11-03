package com.huan.vhr_springboot.config;

/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;*/

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoginUserConfig {
    @Resource
    private IgnoreRequestItem ignoreRequestItem;

    @Bean
    public RequestMatcher[] requestMatchers(){
        List<RequestMatcher> requestMatchers = new ArrayList<>();
        ignoreRequestItem.getGet().forEach(path -> requestMatchers.add(new AntPathRequestMatcher(path,"GET")));
        ignoreRequestItem.getPost().forEach(path -> requestMatchers.add(new AntPathRequestMatcher(path,"POST")));
        return requestMatchers.toArray(new RequestMatcher[0]);
    }
}
