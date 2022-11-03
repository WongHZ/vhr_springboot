package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huan.vhr_springboot.entity.House;
import com.huan.vhr_springboot.mapper.HouseMapper;
import com.huan.vhr_springboot.service.HouseService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HouseServiceimpl implements HouseService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final String PREFIX = "house_";
    @Resource
    HouseMapper houseMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 个人房产数据列表查询。
     * 如有上面语句返回的业主姓名，则精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "oname_" + 当前业主名 + "page_" + 当前页数
     */
    @Transactional
    public IPage<House> selectAllHouse(Long pageNo, String ownerName) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","oname_" + ownerName + "_page_" + pageNo)){
            LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StrUtil.isNotBlank(ownerName),House::getOName,ownerName);
            IPage<House> house_list = houseMapper
                    .selectPage(new Page<>(pageNo,PAGESIZE),wrapper);
            redisTemplate.opsForHash().put(PREFIX + "page","oname_" + ownerName + "_page_" + pageNo,house_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("house页面数据从数据库拿");
            return house_list;
        }else {
            IPage<House> house_list = (IPage<House>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","oname_" + ownerName + "_page_" + pageNo);
            log.info("house页面数据从redis拿");
            return house_list;
        }
    }

    /**
     * 根据个人房产id查找该条数据
     * @param hid 该条数据的id
     * @return 该条数据
     * 2、redis类型hash,key: PREFIX + "data", hashkey: "hid_" + 当前hid
     */
    @Transactional
    public House selectHouseByHid(Long hid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","hid_" + hid)){
            House house = houseMapper.selectById(hid);
            redisTemplate.opsForHash().put(PREFIX + "data","hid_" + hid,house);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("id为{}的数据从数据库拿",hid);
            return house;
        }else {
            House house = (House) redisTemplate.opsForHash()
                    .get(PREFIX + "data","hid_" + hid);
            log.info("id为{}的数据从redis拿",hid);
            return house;
        }
    }

    /**
     * 根据房产证号查询id
     * @param hcode 房产号
     * @return 小区id
     * 3、redis类型hash,key: PREFIX + "hcode", hashkey: 当前房产证号
     */
    @Transactional
    public Long selectHidByHcode(String hcode) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "hcode",hcode)){
            LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(House::getHId).eq(House::getHCode,hcode);
            House house = houseMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(house)){
                return 0L;
            }else {
                redisTemplate.opsForHash().put(PREFIX + "hcode",hcode,house.getHId());
                redisTemplate.expire(PREFIX + "hcode",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
                log.info("房产证为{}的数据从数据库拿",hcode);
                return house.getHId();
            }
        }else {
            Long hid = (Long) redisTemplate.opsForHash().get(PREFIX + "hcode",hcode);
            log.info("房产证为{}的数据从redis拿",hcode);
            return hid;
        }
    }

    /**
     * 根据房产名找该条数据id
     * @param name
     * @return
     */
    @Transactional
    public Long selectHidByHname(String name){
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(House::getHId).eq(House::getHName,name);
        House house = houseMapper.selectOne(wrapper);
        if(StrUtil.isBlankIfStr(house)){
            return 0L;
        }else {
            return house.getHId();
        }
    }

    //增删改操作

    /**
     * 添加个人房产信息
     * @param house 新增house信息
     * @return sql结果
     */
    @Transactional
    public Integer addHouse(House house) {
        Integer result = houseMapper.insert(house);
        return result;
    }

    /**
     * 更新个人房产信息
     * @param house
     * @return
     */
    @Transactional
    public Integer updateHouse(House house) {
        LambdaUpdateWrapper<House> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(House::getHId,house.getHId()).set(House::getCId,house.getCId()).set(House::getCName,house.getCName())
                .set(House::getBName,house.getBName()).set(House::getHCode,house.getHCode()).set(House::getHName,house.getHName())
                .set(House::getOId,house.getOId()).set(House::getOName,house.getOName()).set(House::getOPhone,house.getOPhone())
                .set(House::getRoomNum,house.getRoomNum()).set(House::getUnit,house.getUnit()).set(House::getFloor,house.getFloor())
                .set(House::getLiveTime,house.getLiveTime());
        Integer result = houseMapper.update(null,wrapper);
        return result;
    }

    /**
     * 删除该条数据
     * @param id
     * @return
     */
    @Transactional
    public Integer deleteHouseById(Long id) {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getHId,id);
        Integer result = houseMapper.delete(wrapper);
        return result;
    }
}
