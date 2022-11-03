package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.House;
import com.huan.vhr_springboot.entity.ParkingUse;
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
public class ParkingUseAop {
    private static final String PREFIX = "parker_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对parker新增数据切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ParkingUseServiceimpl.addParkingUse(..))")
    public void addParkerPointCut(){
    }

    private void addMethod(Collection<String> collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1{},2{},3{}----",time,PREFIX + "page",
                        PREFIX + "total",PREFIX + "pagenum");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对parker新增数据进行双删策略
    @Around("addParkerPointCut()")
    public Object addParker(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        Object[] obj = joinPoint.getArgs();
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对parker修改切面
     * 1、4
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ParkingUseServiceimpl.upParkingUse(..))")
    public void upParkerPointCut(){
    }

    private void updateMethod(ParkingUse parkingUse, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "id","cid_" + parkingUse.getCid().toString());
                log.info("----{}毫秒后延迟house相关数据删除完毕，删除缓存key为：1页面{},4{}----",
                        time,PREFIX + "page",PREFIX + "cid_" + parkingUse.getCid());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对parker修改进行双删策略
    @Around("upParkerPointCut()")
    public Object upParker(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        ParkingUse parkingUse = (ParkingUse) obj[0];
        updateMethod(parkingUse,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(parkingUse,750);//第二遍删除
        return result;
    }

    /**
     * 对parker删除数据切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ParkingUseServiceimpl.parkingusedel(..))")
    public void delParkerPointCut(){
    }

    private void delMethod(Collection<String> collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1{},2{},3{},4{}----",time,PREFIX + "page",
                        PREFIX + "total",PREFIX + "pagenum",PREFIX + "id");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对parker删除数据进行双删策略
    @Around("delParkerPointCut()")
    public Object delParker(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "id");
        Object[] obj = joinPoint.getArgs();
        delMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,750);//第二遍删除
        return result;
    }
}
