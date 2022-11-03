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
public class BuildingAop {
    private static final String PREFIX = "building_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对building修改切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.BuildingServiceimpl.addBuilding(..))")
    public void addBuildingPointCut(){
    }

    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.BuildingServiceimpl.delBuilding(..))")
    public void delBuildingPointCut(){
    }

    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.BuildingServiceimpl.delByCommunityName(..))")
    public void delBuildingByComPointCut(){
    }

    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.BuildingServiceimpl.updateBuildingNameByCommunityName(..))")
    public void updatePointCut(){
    }

    private void buildingMethod(Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                log.info("----{}毫秒后延迟building相关数据删除完毕，删除缓存key为：1号页面{}----",time,PREFIX + "page");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对building增进行双删策略
    @Around("addBuildingPointCut()")
    public Object buildingAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        buildingMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        buildingMethod(750);//第二遍删除
        return result;
    }

    //对building删进行双删策略
    @Around("delBuildingPointCut()")
    public Object buildingdel(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        buildingMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        buildingMethod(750);//第二遍删除
        return result;
    }

    //对building小区删进行双删策略
    @Around("delBuildingByComPointCut()")
    public Object buildingdelbycommunity(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        buildingMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        buildingMethod(750);//第二遍删除
        return result;
    }

    //对building更新进行双删策略
    @Around("updatePointCut()")
    public Object updatebuilding(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        buildingMethod(0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        buildingMethod(750);//第二遍删除
        return result;
    }
}
