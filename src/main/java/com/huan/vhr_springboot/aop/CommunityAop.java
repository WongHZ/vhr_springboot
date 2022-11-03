package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Community;
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
public class CommunityAop {
    private static final String PREFIX = "community_";
    @Resource
    RedisTemplate redisTemplate;


    /**
     * 对community新增切面
     * 1567
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.CommunityServiceimpl.insertCommunity(..))")
    public void communityAddPointCut(){
    }

    private void addMethod(Collection collection,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟community相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对community新增数据进行双删策略
    @Around("communityAddPointCut()")
    public Object communityAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "names");
        collection.add("total_building");
        collection.add("total_house");
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对community更新切面
     * 123458
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.CommunityServiceimpl.updateCommunityById(..))")
    public void communityUpPointCut(){
    }

    private void updateMethod(Community community,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.opsForHash().delete(PREFIX + "data","cid_" + community.getCid().toString());
                redisTemplate.opsForHash().delete(PREFIX + "data","cname_" + community.getCname());
                redisTemplate.delete(PREFIX + "names");
                log.info("----{}毫秒后延迟community相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对community新增数据进行双删策略
    @Around("communityUpPointCut()")
    public Object communityUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Community community = (Community) obj[0];
        updateMethod(community,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(community,750);//第二遍删除
        return result;
    }

    /**
     * 对community更新切面
     * 123458
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.CommunityServiceimpl.deleteCommunityById(..))")
    public void communityDelPointCut(){
    }

    private void delMethod(Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.delete(PREFIX + "data");
                redisTemplate.delete(PREFIX + "names");
                redisTemplate.delete("total_building");
                redisTemplate.delete("total_house");
                log.info("----{}毫秒后延迟community相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }

    //对community删除数据进行双删策略
    @Around("communityDelPointCut()")
    public Object communityDel(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Long id = (Long) obj[0];
        delMethod(id,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(id,750);//第二遍删除
        return result;
    }
}
