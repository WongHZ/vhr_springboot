package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.ParkingUse;
import com.huan.vhr_springboot.entity.Personnel;
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
public class PersonnelAop {
    private static final String PREFIX = "persons_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对person新增数据切面
     * 1、2、3、5
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.PersonnelServiceimpl.personneladd(..))")
    public void addPersonPointCut(){
    }

    private void addMethod(Collection<String> collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟person相关数据删除完毕，删除缓存key为：1{},2{},3{},5{}----",time,PREFIX + "page",
                        PREFIX + "total",PREFIX + "pagenum",PREFIX + "wel_tenant");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对person新增数据进行双删策略
    @Around("addPersonPointCut()")
    public Object addPerson(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "wel_tenant");
        Object[] obj = joinPoint.getArgs();
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对person修改切面
     * 1、4、5、6、7
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.PersonnelServiceimpl.personnelupdate(..))")
    public void upPersonPointCut(){
    }

    private void updateMethod(Personnel personnel, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","pid_" + personnel.getPid().toString());
                redisTemplate.delete(PREFIX + "wel_tenant");
                redisTemplate.opsForHash().delete(PREFIX + "pname", "pid_"+ personnel.getPid().toString());
                redisTemplate.opsForHash().delete(PREFIX + "pname", "pname_"+ personnel.getPname());
                redisTemplate.opsForHash().delete(PREFIX + "temp_name","pname_" + personnel.getPname());
                log.info("----{}毫秒后延迟person相关数据删除完毕，删除缓存key为：1{},4{},5{},6{},7{}----",
                        time,PREFIX + "page",PREFIX + "data_" + personnel.getPid(),PREFIX + "wel_tenant",
                        PREFIX + "pname_"+ personnel.getPid(),PREFIX + "pname_"+ personnel.getPname());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对person修改进行双删策略
    @Around("upPersonPointCut()")
    public Object upPerson(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Personnel personnel = (Personnel) obj[0];
        updateMethod(personnel,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(personnel,750);//第二遍删除
        return result;
    }

    /**
     * 对person删除数据切面
     * 1、2、3、4、5、6、7
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.PersonnelServiceimpl.personneldel(..))")
    public void delPersonPointCut(){
    }

    private void delMethod(Collection<String> collection, Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                redisTemplate.opsForHash().delete(PREFIX + "data","pid_" + id);
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1{},2{},3{},4{},5{},67{}----",
                        time,PREFIX + "page", PREFIX + "total",PREFIX + "pagenum"
                        ,PREFIX + "data_" + id,PREFIX + "wel_tenant",PREFIX + "pname");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对person删除数据进行双删策略
    @Around("delPersonPointCut()")
    public Object delParker(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "wel_tenant");
        collection.add(PREFIX + "pname");
        collection.add(PREFIX + "temp_name");
        Object[] obj = joinPoint.getArgs();
        Long id = (Long) obj[0];
        delMethod(collection,id,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,id,750);//第二遍删除
        return result;
    }
}
