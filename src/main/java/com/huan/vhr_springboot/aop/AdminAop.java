package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.LoginUser;
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
import java.util.concurrent.*;
@Slf4j
@Aspect
@Component
@EnableAspectJAutoProxy
public class AdminAop{
    private ThreadPoolExecutor service = new ThreadPoolExecutor(1,5, 30, TimeUnit.SECONDS, new SynchronousQueue<>());
    private static final String PREFIX = "admin_";
    @Resource
    RedisTemplate redisTemplate;


    /**
     * 对admin更改状态切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AdminServiceimpl.changeStatus(..))")
    public void adminChangeStatusPointCut(){
    }

    private void changeMethod(Integer page, Integer time){
        service.execute(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.opsForHash().delete(PREFIX + "page","page_" + page);
                redisTemplate.delete(PREFIX + "data");
                redisTemplate.delete(PREFIX + "name");
                log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1号所在页面{},7号{}----",time,"page_" + page,PREFIX + "name");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //对admin更改状态进行双删策略
    @Around("adminChangeStatusPointCut()")
    public Object adminChangeStatus(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Integer page = (Integer) obj[2];
        changeMethod(page,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        changeMethod(page,750);//第二遍删除
        return result;
    }

    /**
     * 对admin新增数据切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AdminServiceimpl.adminadd(..))")
    public void adminAddPointCut(){
    }

    private void addMethod(Collection<String> collection,Integer time){
        service.execute(() -> {
                    try {
                        Thread.sleep(time);
                        redisTemplate.delete(collection);
                        log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1号{},2号{},3号{},7号{}----",time,PREFIX + "page",
                                PREFIX + "total",PREFIX + "pagenum",PREFIX + "name");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
        });
    }

    //对admin新增数据进行双删策略
    @Around("adminAddPointCut()")
    public Object adminAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "name");
        Object[] obj = joinPoint.getArgs();
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对admin删除数据切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AdminServiceimpl.admindel(..))")
    public void adminDelPointCut(){
    }

    private void delMethod(Collection<String> collection,String id,Integer page,Integer time){
        service.execute(() -> {
                    try {
                        Thread.sleep(time);
                        redisTemplate.delete(collection);
                        redisTemplate.opsForHash().delete(PREFIX + "page","page_" + page);
                        log.info("----{}毫秒后延迟admin相关数据删除完毕，删除缓存key为：1号所在页面{},2号{},3号{},6号id为{}，7号{}----",
                                time,"page_" + page,PREFIX + "total",PREFIX + "pagenum","id_" + id,PREFIX + "name");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
        });
    }

    //对admin删除数据进行双删策略
    @Around("adminDelPointCut()")
    public Object adminDel(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "name");
        collection.add(PREFIX + "data");
        Object[] obj = joinPoint.getArgs();
        String id = obj[0].toString();
        Integer page = (Integer) obj[1];
        delMethod(collection,id,page,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,id,page,750);//第二遍删除
        return result;
    }

    /**
     * 对admin删除数据切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AdminServiceimpl.updateUser(..))")
    public void adminUpPointCut(){
    }

    private void upMethod(Collection<String> collection,LoginUser loginUser,Integer time){
        service.execute(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                redisTemplate.opsForHash().delete(PREFIX + "data","uid_" + loginUser.getUid());
                redisTemplate.opsForHash().delete(PREFIX + "data","uname_" + loginUser.getUname());
                log.info("----{}毫秒后延迟admin相关数据删除完毕----", time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //对admin删除数据进行双删策略
    @Around("adminUpPointCut()")
    public Object adminUp(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "name");
        collection.add(PREFIX + "page");
        Object[] obj = joinPoint.getArgs();
        LoginUser loginUser = (LoginUser) obj[0];
        upMethod(collection,loginUser,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        upMethod(collection,loginUser,750);//第二遍删除
        return result;
    }

    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.AdminServiceimpl.updatePass(..))")
    public void passUpPointCut(){
    }

    private void passUpMethod(Long uid,Integer time){
        service.execute(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.opsForHash().delete(PREFIX + "data","uid_" + uid.toString());
                log.info("----{}毫秒后延迟admin相关数据删除完毕----", time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    //对admin删除数据进行双删策略
    @Around("passUpPointCut()")
    public Object passUp(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Long uid = (Long) obj[0];
        passUpMethod(uid,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        passUpMethod(uid,750);//第二遍删除
        return result;
    }
}
