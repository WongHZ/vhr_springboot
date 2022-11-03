package com.huan.vhr_springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        //把顾客图片资源放到项目外并映射
        registry.addResourceHandler("/cimage/**").addResourceLocations("file:F:/OneDrive/vhr_springboot_image/community/");
        registry.addResourceHandler("/pimage/**").addResourceLocations("file:F:/OneDrive/vhr_springboot_image/personnel/");
        registry.addResourceHandler("/carimage/**").addResourceLocations("file:F:/OneDrive/vhr_springboot_image/car/");
        registry.addResourceHandler("/uimage/**").addResourceLocations("file:F:/OneDrive/vhr_springboot_image/user/");
        registry.addResourceHandler("/templates").addResourceLocations("classpath:/templates/");
        // 解决swagger的js文件访问问题
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/**");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

}
