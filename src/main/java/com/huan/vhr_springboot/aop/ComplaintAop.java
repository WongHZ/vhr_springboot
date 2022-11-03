package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Complaint;
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
public class ComplaintAop {
    private static final String PREFIX = "complaint_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对complaint新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ComplaintServiceimpl.addComplaint(..))")
    public void addComplaintPointCut(){
    }

    private void addMethod(Collection collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟complaint相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对complaint新增数据进行双删策略
    @Around("addComplaintPointCut()")
    public Object addComplaint(ProceedingJoinPoint joinPoint) throws Throwable {
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
     * 对complaint更新切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ComplaintServiceimpl.upComplaint(..))")
    public void upComplaintPointCut(){
    }

    private void updateMethod(Complaint complaint, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","cpid_" + complaint.getCpid());
                log.info("----{}毫秒后延迟complaint相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对complaint更新数据进行双删策略
    @Around("upComplaintPointCut()")
    public Object upComplaint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Complaint complaint = (Complaint) obj[0];
        updateMethod(complaint,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(complaint,750);//第二遍删除
        return result;
    }

    /**
     * 对complaint删除切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ComplaintServiceimpl.delComplaint(..))")
    public void delComplaintPointCut(){
    }

    private void delMethod(Collection collection, Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                redisTemplate.opsForHash().delete(PREFIX + "data","cpid_" + id);
                log.info("----{}毫秒后延迟complaint相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对complaint删除数据进行双删策略
    @Around("delComplaintPointCut()")
    public Object delComplaint(ProceedingJoinPoint joinPoint) throws Throwable {
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

    /**
     * 对complaint状态切换切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.ComplaintServiceimpl.changeStatus(..))")
    public void cstatusComplaintPointCut(){
    }

    private void cstatusMethod(Collection collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟complaint相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对complaint新增数据进行双删策略
    @Around("cstatusComplaintPointCut()")
    public Object cstatusComplaint(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        cstatusMethod(collection, 0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        cstatusMethod(collection, 750);//第二遍删除
        return result;
    }
}
