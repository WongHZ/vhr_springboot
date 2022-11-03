package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huan.vhr_springboot.entity.Building;
import com.huan.vhr_springboot.mapper.BuildingMapper;
import com.huan.vhr_springboot.service.BuildingService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BuildingServiceimpl implements BuildingService {
    private static final Long PAGESIZE = 10L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 60;
    private static final String PREFIX = "building_";

    @Resource
    BuildingMapper buildingMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;


    /**
     * 楼栋数据列表查询。
     * 如有上面语句返回的小区姓名
     *  则查找该小区姓名旗下的楼栋信息。
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "page_" + 当前页数
     */
    @Transactional
    public IPage<Building> selectAllBuilding(Long pageNo, String communityName) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","cname_" + communityName + "page_" + pageNo)) {
            LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StrUtil.isNotBlank(communityName), Building::getCname, communityName)
                    .orderByAsc(Building::getCid).orderByAsc(Building::getName);
            IPage<Building> building_list = buildingMapper
                    .selectPage(new Page<>(pageNo, PAGESIZE), wrapper);
            redisTemplate.opsForHash().put(PREFIX + "page","cname_" + communityName + "page_" + pageNo,building_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("name为{}的building页面数据从数据库拿",communityName);
            return building_list;
        }else {
            IPage<Building> building_list = (IPage<Building>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","cname_" + communityName + "page_" + pageNo);
            log.info("name为{}的building页面数据从redis拿",communityName);
            return building_list;
        }
    }

    //以下为增删改操作
    /**
     * 添加楼栋，一般在添加小区时会默认五栋楼
     * 这里先查询是否已有同名building，没有再添加
     * @param building
     * @return
     */
    @Transactional
    public Integer addBuilding(Building building){
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Building::getCname,Building::getName).eq(Building::getCname,building.getCname())
                .eq(Building::getName,building.getName());
        Building selectBuilding = buildingMapper.selectOne(wrapper);
        Integer result = 0;
        if(StrUtil.isBlankIfStr(selectBuilding)){
            result = buildingMapper.insert(building);
        }
        return result;
    }

    /**
     * 根据传来的小区名字删除相关在楼栋表的数据
     * @param name
     * @return
     */
    @Transactional
    public Integer delByCommunityName(String name) {
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getCname,name);
        return buildingMapper.delete(wrapper);
    }

    /**
     * 小区如果修改了总户数就需要把这里修改
     * @param name
     * @param total
     * @return
     */
    @Transactional
    public Integer updateBuildingNameByCommunityName(String name,Integer total){
        LambdaUpdateWrapper<Building> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Building::getTotalhouses,total).eq(Building::getCname,name);
        return buildingMapper.update(null,wrapper);
    }

    /**
     * 根据id删除楼栋名
     * @param id
     * @return
     */
    @Transactional
    public Integer delBuilding(Long id){
        LambdaQueryWrapper<Building> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Building::getId,id);
        return buildingMapper.delete(wrapper);
    }
}
