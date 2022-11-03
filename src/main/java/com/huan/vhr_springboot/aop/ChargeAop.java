package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Charge;
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
public class ChargeAop {
    private static final String PREFIX = "charge_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对charge新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ChargeServiceimpl.addCharge(..))")
    public void addChargePointCut(){
    }

    private void addMethod(Collection collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟charge相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对repair新增数据进行双删策略
    @Around("addChargePointCut()")
    public Object addRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对charge更新切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ChargeServiceimpl.upCharge(..))")
    public void upChargePointCut(){
    }

    private void updateMethod(Charge charge, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","code_" + charge.getCode());
                redisTemplate.opsForHash().delete(PREFIX + "data","chid_" + charge.getChid());
                redisTemplate.opsForHash().delete(PREFIX + "data","cid_" + charge.getCid());
                log.info("----{}毫秒后延迟charge相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对charge更新数据进行双删策略
    @Around("upChargePointCut()")
    public Object upDevice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Charge charge = (Charge) obj[0];
        updateMethod(charge,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(charge,750);//第二遍删除
        return result;
    }

    /**
     * 对charge删除切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ChargeServiceimpl.delCharge(..))")
    public void delRepairPointCut(){
    }

    private void delMethod(Collection collection,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟repair相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对charge删除数据进行双删策略
    @Around("delRepairPointCut()")
    public Object delRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "data");
        delMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,750);//第二遍删除
        return result;
    }

}
