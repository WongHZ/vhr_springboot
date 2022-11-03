package com.huan.vhr_springboot;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@EnableCaching
@SpringBootApplication
public class vhr_Application {
    public static void main(String[] args) {
        SpringApplication.run(vhr_Application.class,args);
    }
}
