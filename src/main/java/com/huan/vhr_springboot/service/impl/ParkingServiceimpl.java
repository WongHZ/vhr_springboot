package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Parking;
import com.huan.vhr_springboot.mapper.ParkingMapper;
import com.huan.vhr_springboot.service.ParkingService;
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
public class ParkingServiceimpl implements ParkingService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "parking_";

    @Resource
    ParkingMapper parkingMapper;
    @Resource
    MakeUtil makeUtil;
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 查询车辆数据并分页
     * if 房产证id 有 then 精确查询所属车辆数据，else 查询全部数据
     * @param page 当前页数
     * @param cid 该条房产证id
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "cid_" + 当前cid +"_page_" + 当前页数
     */
    @Transactional
    public List<Parking> selectAllParking(Long page, Long cid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","cid_" + cid +"_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Parking> parkings_list = parkingMapper.selectPage(pageNo,PAGESIZE,cid);
            redisTemplate.opsForHash().put(PREFIX + "page","cid_" + cid +"_page_" + page,parkings_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("parking页面数据从数据库拿");
            return parkings_list;
        }else {
            List<Parking> parkings_list = (List<Parking>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","cid_" + cid +"_page_" + page);
            log.info("parking页面数据从redis拿");
            return parkings_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if cid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else cid等于0 then 返回全部数据的数量
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "cid_" + 当前cid
     */
    @Transactional
    public Integer total(Long cid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","cid_" + cid.toString())){
            Integer total;
            if(cid != 0 && cid != null){
                total = parkingMapper.countTotalByCid(cid);
            }else {
                total = parkingMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","cid_" + cid,total);
            log.info("cid为{}的总数据量从数据库拿",cid);
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","cid_" + cid);
            log.info("cid为{}的总数据量从redis拿",cid);
            return total;
        }
    }

    /**
     * 计算共有几页，公式：页数=总数据量/每页显示量
     *  其中余数向上取整
     * @param total 总数据量
     * 3、redis类型hash,key: PREFIX + "pagenum", hashkey: "total_" + 总数据量
     */
    @Transactional
    public Integer pageNum(Integer total) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "pagenum","total_" + total.toString())){
            double doubleNum = Math.ceil((double)total/PAGESIZE.intValue());
            Integer pageNum = (int)doubleNum;
            redisTemplate.opsForHash().put(PREFIX + "pagenum","total_" + total,pageNum);
            redisTemplate.expire(PREFIX + "pagenum",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("parking的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("parking的总页数从redis拿");
            return pageNum;
        }
    }

    /**
     * 给ParkingUse车位使用管理模块使用，根据车位编号找id
     * @param carCode
     * 4、redis类型hash,key: PREFIX + "code", hashkey: "code_" + 车牌编号
     */
    @Transactional
    public Parking selectParkingByCarcode(String carCode){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "code","carcode_" + carCode)){
            LambdaQueryWrapper<Parking> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Parking::getId,Parking::getStatus).eq(Parking::getCode,carCode);
            Parking parking = parkingMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(parking)){
                return null;
            }
            redisTemplate.opsForHash().put(PREFIX + "code","code_" + carCode,parking);
            redisTemplate.expire(PREFIX + "code",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("车牌号为{}的车位从数据库拿",carCode);
            return parking;
        }else {
            Parking parking = (Parking) redisTemplate.opsForHash()
                    .get(PREFIX + "code","code_" + carCode);
            log.info("车牌号为{}的车位从redis拿",carCode);
            return parking;
        }
    }

    /**
     * 根据车位编号查询该条数据的id
     * @param parkingCode
     * 5、redis类型hash,key: PREFIX + "code", hashkey: "parkingcode_" + 当前车位编号
     */
    @Transactional
    public Long selectParkingByparkingcode(String parkingCode){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "code","parkingcode_" + parkingCode)){
            LambdaQueryWrapper<Parking> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Parking::getId).eq(Parking::getCode,parkingCode);
            Parking parking = parkingMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(parking)){
                redisTemplate.opsForHash().put(PREFIX + "code","parkingcode_" + parkingCode,0L);
                redisTemplate.expire(PREFIX,makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
                return 0L;
            }
            redisTemplate.opsForHash().put(PREFIX + "code","parkingcode_" + parkingCode,parking.getId());
            redisTemplate.expire(PREFIX,makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("车位编号为{}的车位从数据库拿",parking.getId());
            return parking.getId();
        }else {
            Long id = (Long) redisTemplate.opsForHash().get(PREFIX + "code","parkingcode_" + parkingCode);
            log.info("车位编号为{}的车位从redis拿",id);
            return id;
        }
    }

    //增删改操作

    /**
     * 修改该条数据的状态
     * @param id
     * @param status
     */
    @Transactional
    public Integer changeStatus(Long id,Integer status){
        LambdaUpdateWrapper<Parking> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Parking::getStatus,status).eq(Parking::getId,id);
        Integer result = parkingMapper.update(null,wrapper);
        return result;
    }

    /**
     * 新增停车位
     * @param parking
     */
    @Transactional
    public Integer parkingadd(Parking parking){
        Integer result = parkingMapper.insert(parking);
        return result;
    }

    /**
     * 删除停车位
     * @param id
     */
    @Transactional
    public Integer parkingdel(Long id){
        Integer result = parkingMapper.deleteById(id);
        return result;
    }
}
