package com.huan.vhr_springboot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class AuthRoleAop {
    private static final String PREFIX = "authrole_";
    @Resource
    RedisTemplate redisTemplate;
    /**
     * 对authrole更改状态切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AuthRoleServiceimpl.changeStatus(..))")
    public void authroleChangeStatusPointCut(){
    }

    private void changeMethod(Integer page,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.opsForHash().delete(PREFIX + "url","admin");
                redisTemplate.opsForHash().delete(PREFIX + "page","page_" + page);
                redisTemplate.delete(PREFIX + "name");
                log.info("----{}毫秒后延迟authrole相关数据删除完毕，删除缓存key为：1号所在页面{},权限表{}----",time,"page_" + page,PREFIX + "name");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对authrole更改状态进行双删策略
    @Around("authroleChangeStatusPointCut()")
    public Object authroleChangeStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Integer page = (Integer) obj[2];
        changeMethod(page,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        changeMethod(page,750);//第二遍删除
        return result;
    }

    /**
     * 对authrole新增数据切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AuthRoleServiceimpl.authorityadd(..))")
    public void authroleAddPointCut(){
    }

    private void authroleMethod(Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.opsForHash().delete(PREFIX + "url","admin");
                redisTemplate.delete(PREFIX + "page");
                log.info("----{}毫秒后延迟authrole相关数据删除完毕，删除缓存key为：1号所在页面{},权限表{}----",time,PREFIX + "page",
                        PREFIX + "page");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对authrole新增数据进行双删策略
    @Around("authroleAddPointCut()")
    public Object authroleAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        authroleMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        authroleMethod(750);//第二遍删除
        return result;
    }
}
