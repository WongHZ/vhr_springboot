package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.House;
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
public class HouseAop {
    private static final String PREFIX = "house_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对house更改状态切面
     * 1
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.HouseServiceimpl.addHouse(..))")
    public void addHousePointCut(){
    }

    private void addMethod(House house,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                log.info("----{}毫秒后延迟house相关数据删除完毕，删除缓存key为：1号页面{}----",
                        time,PREFIX + "page");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对house新增进行双删策略
    @Around("addHousePointCut()")
    public Object addHouse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        House house = (House) obj[0];
        addMethod(house,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(house,750);//第二遍删除
        return result;
    }

    /**
     * 对house修改切面
     * 1、2、3
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.HouseServiceimpl.updateHouse(..))")
    public void upHousePointCut(){
    }

    private void updateMethod(House house,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","hid_" + house.getHId().toString());
                redisTemplate.opsForHash().delete(PREFIX + "hcode",house.getHCode());
                log.info("----{}毫秒后延迟house相关数据删除完毕，删除缓存key为：1号页面{},2号{},3号{}----",
                        time,PREFIX + "page",PREFIX + "data_" + house.getHId(),PREFIX + "hcode_" + house.getHCode());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对house修改进行双删策略
    @Around("upHousePointCut()")
    public Object upHouse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        House house = (House) obj[0];
        updateMethod(house,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(house,750);//第二遍删除
        return result;
    }

    /**
     * 对house删除切面
     * 1、2、3
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.HouseServiceimpl.deleteHouseById(..))")
    public void delHousePointCut(){
    }

    private void delMethod(Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","hid_" + id.toString());
                redisTemplate.delete(PREFIX + "hcode");
                log.info("----{}毫秒后延迟house相关数据删除完毕，删除缓存key为：1号页面{},2号{},3号{}----",
                        time,PREFIX + "page",PREFIX + "data_" + id,PREFIX + "hcode_");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对house删除进行双删策略
    @Around("delHousePointCut()")
    public Object delHouse(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Long id = (Long) obj[0];
        delMethod(id,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(id,750);//第二遍删除
        return result;
    }
}
