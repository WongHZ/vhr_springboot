package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Paylist;
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
public class PaylistAop {
    private static final String PREFIX = "paylist_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对paylist新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.PaylistServiceimpl.addPaylist(..))")
    public void addPaylistPointCut(){
    }

    private void addMethod(Collection collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟paylist相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对paylist新增数据进行双删策略
    @Around("addPaylistPointCut()")
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
     * 对paylist更新切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.PaylistServiceimpl.upPaylist(..))")
    public void upPaylistPointCut(){
    }

    private void updateMethod(Paylist paylist, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","paid_" + paylist.getPaid());
                log.info("----{}毫秒后延迟repair相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对paylist更新数据进行双删策略
    @Around("upPaylistPointCut()")
    public Object upPaylist(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Paylist paylist = (Paylist) obj[0];
        updateMethod(paylist,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(paylist,750);//第二遍删除
        return result;
    }

    /**
     * 对paylist删除切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.PaylistServiceimpl.delPaylist(..))")
    public void delPaylistPointCut(){
    }

    private void delMethod(Collection collection, Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                redisTemplate.opsForHash().delete(PREFIX + "data","paid_" + id);
                log.info("----{}毫秒后延迟paylist相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对paylist删除数据进行双删策略
    @Around("delPaylistPointCut()")
    public Object delRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Long id = (Long) obj[0];
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        delMethod(collection,id,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,id,750);//第二遍删除
        return result;
    }
}
