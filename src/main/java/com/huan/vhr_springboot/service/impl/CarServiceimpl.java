package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Car;
import com.huan.vhr_springboot.mapper.CarMapper;
import com.huan.vhr_springboot.service.CarService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CarServiceimpl implements CarService {
    private static final Long PAGESIZE = 6L;
    private static final String PREFIX = "car_";
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    @Resource
    CarMapper carMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查询车辆数据并分页
     * if 房产证id 有 then 精确查询所属车辆数据，else 查询全部数据
     * @param page 当前页数
     * @param houseId 该条房产证id
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "hid_" + 当前个人房产id + "page_" + 当前页数
     */
    @Transactional
    public List<Car> selectAllCar(Long page, Long houseId) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","hid_" + houseId + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Car> cars_list = carMapper.selectPage(pageNo,PAGESIZE,houseId);
            redisTemplate.opsForHash().put(PREFIX + "page","hid_" + houseId + "_page_" + page,cars_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("car页面数据从数据库拿");
            return cars_list;
        }else{
            List<Car> cars_list = (List<Car>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","hid_" + houseId + "_page_" + page);
            log.info("car页面数据从redis拿");
            return cars_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if houseid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else houseid等于0 then 返回全部数据的数量
     * @param houseId
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "hid_" + 当前个人房产id
     */
    @Transactional
    public Integer total(Long houseId) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","hid_" + houseId)){
            if(houseId != 0 && houseId != null){
                Integer total = carMapper.countTotalByHid(houseId);
                redisTemplate.opsForHash().put(PREFIX + "total","hid_" + houseId,total);
                redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
                log.info("car页面数据从数据库拿");
                return total;
            }else {
                Integer total = carMapper.countTotal();
                redisTemplate.opsForHash().put(PREFIX + "total","hid_" + houseId,total);
                redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
                log.info("car页面数据从数据库拿");
                return total;
            }
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","hid_" + houseId);
            log.info("car总数量数据从redis拿");
            return total;
        }
    }

    /**
     * 计算共有几页，公式：页数=总数据量/每页显示量
     *  其中余数向上取整
     * @param total 总数据量
     * 3、redis类型string,key: PREFIX + "pagenum"
     */
    @Transactional
    public Integer pageNum(Integer total) {
        if(! redisTemplate.hasKey(PREFIX + "pagenum")) {
            double doubleNum = Math.ceil((double) total / PAGESIZE.intValue());
            Integer pageNum = (int) doubleNum;
            redisTemplate.opsForValue().set(PREFIX + "pagenum",pageNum,
                    makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("car总页数数据从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForValue().get(PREFIX + "pagenum");
            log.info("car总页数数据从redis拿");
            return pageNum;
        }
    }

    /**
     * 根据车牌号查询车辆id
     * 用于新增车辆判断该车辆是否已在数据库有记录
     * @param carNum 车牌号
     * @return if car实体为空 then 返回id=0, else 返回该条数据id
     */
    @Transactional
    public Long selectCaridByCarnum(String carNum){
        LambdaQueryWrapper<Car> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Car::getId).eq(Car::getCarnum,carNum);
        Car car = carMapper.selectOne(wrapper);
        if(StrUtil.isBlankIfStr(car)){
            Long carId = 0L;
            return carId;
        }else {
            Long carId = car.getId();
            return carId;
        }
    }

    /**
     * 根据id获取车辆信息，用于车辆更新页面
     * if redis get的key对象为空 then 从数据库取出并存入，时间3分钟
     *  else 从redis取出并返回
     * 4、redis类型hash,key: PREFIX + PREFIX + "data", hashkey: "cid_" + 该数据id
     */
    @Transactional
    public Car selectDataById(Long cid){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","cid_" + cid)){
            Car car = carMapper.selectDataById(cid);
            redisTemplate.opsForHash().put(PREFIX + "data","cid_" + cid,car);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("cid为{}的数据从数据库拿",cid);
            return car;
        }else {
            Car car =  (Car) redisTemplate.opsForHash().get(PREFIX + "data","cid_" + cid);
            log.info("cid为{}的数据从redis拿",cid);
            return car;
        }
    }

    /**
     * 根据车牌号码获取车辆信息
     * @param code
     * @return
     * 5、redis类型hash,key: PREFIX + "data", hashkey: "code_" + 车牌号码
     */
    @Transactional
    public Car selectCarBycarCode(String code){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","code_" + code)){
            LambdaQueryWrapper<Car> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Car::getId,Car::getImage).eq(Car::getCarnum,code);
            Car car = carMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(car)){
                return null;
            }
            redisTemplate.opsForHash().put(PREFIX + "data","code_" + code,car);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(MINTTL),TimeUnit.SECONDS);
            log.info("车牌号为{}的数据从数据库拿",code);
            return car;
        }else {
            Car car = (Car) redisTemplate.opsForHash().get(PREFIX + "data","code_" + code);
            log.info("车牌号为{}的数据从redis拿",code);
            return car;
        }
    }

    //以下为增删改操作
    /**
     * 新增车辆信息
     * @param car car实体类
     * @return 操作数据信息
     */
    @Transactional
    public Integer addCar(Car car){
        Integer result = carMapper.insert(car);
        return result;
    }

    /**
     * 更新车辆数据，可更新该条数据绑定的房产证、车牌号、颜色
     *  图片路径以及更新时间
     * @param car
     * @return
     */
    @Transactional
    public Integer updateCar(Car car,Integer page){
        LambdaUpdateWrapper<Car> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Car::getHid,car.getHid()).set(Car::getCarnum,car.getCarnum())
                .set(Car::getColor,car.getColor()).set(Car::getImage,car.getImage())
                .set(Car::getUpdatetime,car.getUpdatetime()).eq(Car::getId,car.getId());
        Integer result = carMapper.update(null,wrapper);
        return result;
    }

    /**
     * 删除该条车辆数据
     * @param car
     * @return
     */
    @Transactional
    public Integer cardel(Car car){
        Integer result = carMapper.deleteById(car.getId());
        return result;
    }
}
