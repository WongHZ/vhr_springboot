package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Device;
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
public class DeviceAop {
    private static final String PREFIX = "device_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对device新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.DeviceServiceimpl.addDevice(..))")
    public void addDevicePointCut(){
    }

    private void addMethod(Collection collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟device相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对device新增数据进行双删策略
    @Around("addDevicePointCut()")
    public Object addDevice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "device_name");
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对device更新切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.DeviceServiceimpl.upDevice(..))")
    public void upDevicePointCut(){
    }

    private void updateMethod(Device device, Integer page, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.opsForHash().delete(PREFIX + "page","cid_" + device.getCid() + "_page_" + page);
                redisTemplate.opsForHash().delete(PREFIX + "page","cid_0" + "_page_" + page);
                redisTemplate.opsForHash().delete(PREFIX + "data","did_" + device.getDid());
                redisTemplate.opsForHash().delete(PREFIX + "data", "dname_" + device.getName());
                redisTemplate.delete(PREFIX + "device_name");
                log.info("----{}毫秒后延迟device相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对device更新数据进行双删策略
    @Around("upDevicePointCut()")
    public Object upDevice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Device device = (Device) obj[0];
        Integer page = (Integer) obj[1];
        updateMethod(device,page,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        updateMethod(device,page,750);//第二遍删除
        return result;
    }

    /**
     * 对device删除切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.DeviceServiceimpl.delDevice(..))")
    public void delDevicePointCut(){
    }

    private void delMethod(Collection collection, Long id,Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                redisTemplate.opsForHash().delete(PREFIX + "data","did_" + id);
                log.info("----{}毫秒后延迟device相关数据删除完毕----",time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对device删除数据进行双删策略
    @Around("delDevicePointCut()")
    public Object delDevice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Long id = (Long) obj[0];
        Collection collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "device_name");
        delMethod(collection,id,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(collection,id,750);//第二遍删除
        return result;
    }
}
