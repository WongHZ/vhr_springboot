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

@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class ParkingAop {
    private static final String PREFIX = "parking_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对parking更改状态切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ParkingServiceimpl.changeStatus(..))")
    public void parkingChangeStatusPointCut(){
    }

    private void changeMethod(Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.delete(PREFIX + "code");
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1号页面{}----",time,PREFIX + "page");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对admin更改状态进行双删策略
    @Around("parkingChangeStatusPointCut()")
    public Object parkingChangeStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        changeMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        changeMethod(750);//第二遍删除
        return result;
    }

    /**
     * 对parking新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ParkingServiceimpl.parkingadd(..))")
    public void addParkingPointCut(){
    }

    private void addMethod(Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "total","cid_0");
                redisTemplate.delete(PREFIX + "pagenum");
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1{},2{},3{}----",
                        time,PREFIX + "page",PREFIX + "total",PREFIX + "pagenum");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对parking新增进行双删策略
    @Around("addParkingPointCut()")
    public Object addParking(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        addMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(750);//第二遍删除
        return result;
    }

    /**
     * 对parking删除切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ParkingServiceimpl.parkingdel(..))")
    public void delParkingPointCut(){
    }

    private void delMethod(Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.delete(PREFIX + "total");
                redisTemplate.delete(PREFIX + "pagenum");
                redisTemplate.delete(PREFIX + "code");
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1{},2{},3{},4、5{}----",
                        time,PREFIX + "page",PREFIX + "total",PREFIX + "pagenum",PREFIX + "code");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对parking新增进行双删策略
    @Around("delParkingPointCut()")
    public Object delParking(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        delMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(750);//第二遍删除
        return result;
    }
}
