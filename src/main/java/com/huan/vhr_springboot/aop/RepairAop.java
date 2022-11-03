package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Device;
import com.huan.vhr_springboot.entity.Repair;
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
public class RepairAop {
    private static final String PREFIX = "repair_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对repair新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.RepairServiceimpl.addRepair(..))")
    public void addRepairPointCut(){
    }

    private void addMethod(Collection collection, Integer time){
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

    //对repair新增数据进行双删策略
    @Around("addRepairPointCut()")
    public Object addRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "wel_list");
        collection.add("wel_repair");
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对repair更新切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.RepairServiceimpl.updateRepair(..))")
    public void upRepairPointCut(){
    }

    private void updateMethod(Repair repair, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.delete(PREFIX + "wel_list");
                redisTemplate.delete("wel_repair");
                redisTemplate.opsForHash().delete(PREFIX + "data","aname_" + repair.getAname());
                redisTemplate.opsForHash().delete(PREFIX + "data","rid_" + repair.getRid());
                log.info("----{}毫秒后延迟repair相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对repair更新数据进行双删策略
    @Around("upRepairPointCut()")
    public Object upRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Repair repair = (Repair) obj[0];
        updateMethod(repair,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(repair,750);//第二遍删除
        return result;
    }

    /**
     * 对repair删除切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.RepairServiceimpl.delRepair(..))")
    public void delRepairPointCut(){
    }

    private void delMethod(Collection collection, Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                redisTemplate.opsForHash().delete(PREFIX + "data","did_" + id);
                log.info("----{}毫秒后延迟repair相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对repair删除数据进行双删策略
    @Around("delRepairPointCut()")
    public Object delRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Long id = (Long) obj[0];
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "wel_list");
        collection.add("wel_repair");
        delMethod(collection,id,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,id,750);//第二遍删除
        return result;
    }

    /**
     * 对repair状态切换切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.RepairServiceimpl.changeStatus(..))")
    public void cstatusRepairPointCut(){
    }

    private void cstatusMethod(Collection collection, Integer time){
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

    //对repair新增数据进行双删策略
    @Around("cstatusRepairPointCut()")
    public Object cstatusRepair(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add("wel_repair");
        cstatusMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        cstatusMethod(collection,750);//第二遍删除
        return result;
    }
}
