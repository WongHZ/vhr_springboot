package com.huan.vhr_springboot.aop;

import com.huan.vhr_springboot.entity.Car;
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
public class CarAop {
    private static final String PREFIX = "car_";
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 对car新增切面
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.CarServiceimpl.addCar(..))")
    public void carAddPointCut(){
    }

    private void addMethod(Collection<String> collection, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(collection);
                log.info("----{}毫秒后延迟car相关数据删除完毕，删除缓存key为：1号{},2号{},3号{}----",time,PREFIX + "page",
                        PREFIX + "total",PREFIX + "pagenum");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对car新增数据进行双删策略
    @Around("carAddPointCut()")
    public Object carAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        Object[] obj = joinPoint.getArgs();
        addMethod(collection,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        addMethod(collection,750);//第二遍删除
        return result;
    }

    /**
     * 对carg更新切面
     * 1当前页数据、hid、2total的hid、3、4、5
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.CarServiceimpl.updateCar(..))")
    public void carUpPointCut(){
    }

    private void upMethod(Car car,Integer page, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "pagenum");
                redisTemplate.opsForHash().delete(PREFIX + "page","hid_0" + "_page_" + page);
                redisTemplate.opsForHash().delete(PREFIX + "page","hid_" + car.getHid() + "_page_" + page);
                redisTemplate.opsForHash().delete(PREFIX + "total","hid_" + car.getHid());
                redisTemplate.opsForHash().delete(PREFIX + "data","cid_" + car.getId());
                redisTemplate.opsForHash().delete(PREFIX + "data","code_" + car.getCarnum());
                log.info("----{}毫秒后延迟car相关数据删除完毕，删除缓存key为：1号所在页面{},1号精准页面,2号hid所在{},3号{},4号{},5号{}----",
                        time,PREFIX + "pagenum",PREFIX + "hid_0" + "page_" + page,PREFIX + "hid_" + car.getHid() + "page_" + page,
                        PREFIX + "total_" + car.getHid(),PREFIX + "data_" + car.getId(),PREFIX + "data_" + car.getCarnum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对car新增数据进行双删策略
    @Around("carUpPointCut()")
    public Object carUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Car car  = (Car) obj[0];
        Integer page = (Integer) obj[1];
        upMethod(car,page,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        upMethod(car,page,750);//第二遍删除
        return result;
    }


    /**
     * 对carg删除切面
     * 1page、2total、3、4、5
     */
    @Pointcut(value = "execution(* com.huan.vhr_springboot.service.impl.CarServiceimpl.cardel(..))")
    public void carDelPointCut(){
    }

    private void delMethod(Car car, Integer time){
        new Thread(() -> {
            try {
                Thread.sleep(time);
                redisTemplate.delete(PREFIX + "page");
                redisTemplate.delete(PREFIX + "total");
                redisTemplate.delete(PREFIX + "pagenum");
                redisTemplate.opsForHash().delete(PREFIX + "data","cid_" + car.getId());
                redisTemplate.opsForHash().delete(PREFIX + "data","code_" + car.getCarnum());
                log.info("----{}毫秒后延迟car相关数据删除完毕，删除缓存key为：1号{},2号{},3号{}----",time,PREFIX + "page",
                        PREFIX + "total",PREFIX + "pagenum",PREFIX + "data_" + car.getId(),
                        PREFIX + "data_" + car.getCarnum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //对car新增数据进行双删策略
    @Around("carDelPointCut()")
    public Object carDel(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] obj = joinPoint.getArgs();
        Car car  = (Car) obj[0];
        delMethod(car,0);//第一遍删除
        Object result = joinPoint.proceed(obj);//执行原方法
        delMethod(car,750);//第二遍删除
        return result;
    }
}
