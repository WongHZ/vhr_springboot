package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huan.vhr_springboot.entity.Community;
import com.huan.vhr_springboot.mapper.CommunityMapper;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CommunityServiceimpl implements CommunityService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "community_";
    @Resource
    CommunityMapper communityMapper;
    @Resource
    MakeUtil makeUtil;
    @Resource
    RedisTemplate redisTemplate;



    /**
     * 小区数据列表查询。
     * 如有上面语句返回的管理员姓名
     *  则查找该管理员负责的小区。
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "aname_" + 管理员姓名 + "_page_" + 当前页数
     */
    @Transactional
    public IPage<Community> selectAllCommunity(Long pageNo,String adminName) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","aname_" + adminName + "_page_" + pageNo)){
            LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(StrUtil.isNotBlank(adminName),Community::getManage,adminName);
            IPage<Community> community_list = communityMapper
                    .selectPage(new Page<>(pageNo,PAGESIZE),wrapper);
            redisTemplate.opsForHash().put(PREFIX + "page","aname_" + adminName + "_page_" + pageNo,community_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("community页面数据从数据库拿");
            return community_list;
        }else {
            IPage<Community> community_list = (IPage<Community>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","aname_" + adminName + "_page_" + pageNo);
            log.info("community页面数据从redis拿");
            return community_list;
        }
    }

    /**
     * 根据cid或cname查询数据
     * 前提为：cid=0L时用cname查询；cid != 0L时cid查询
     * 不能为双方都为null
     * @param cid
     * @param cname
     * 3、redis类型hash,key: PREFIX + "data", hashkey: "cid_" + 当前id
     * 4、redis类型hash,key: PREFIX + "data", hashkey: "cname_" + 当前cname
     */
    @Transactional
    public Community selectDataByCidOrCname(Long cid,String cname){
        LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper();
        wrapper.eq(cid != 0L,Community::getCid,cid)
                .eq(StrUtil.isNotBlank(cname),Community::getCname,cname)
                .select(Community::getCname,Community::getC_code,Community::getAddress
                ,Community::getArea,Community::getDeveloper,Community::getEstate_company,Community::getGreening_rate
                ,Community::getTotalbuildings,Community::getTotalhouses,Community::getImage
                ,Community::getManage);
        if(cid == 0L){
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","cname_" + cname)){
                Community community = communityMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","cname_" + cname,community);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("cname为{}的数据从数据库拿",cname);
                return community;
            }else {
                Community community = (Community) redisTemplate.opsForHash()
                        .get(PREFIX + "data","cname_" + cname);
                log.info("cname为{}的数据从redis拿",cname);
                return community;
            }
        }else{
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","cid_" + cid)){
                Community community = communityMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","cid_" + cid,community);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("cid为{}的数据从数据库拿",cid);
                return community;
            }else {
                Community community = (Community) redisTemplate.opsForHash()
                        .get(PREFIX + "data","cid_" + cid);
                log.info("cid为{}的数据从redis拿",cid);
                return community;
            }
        }
    }

    /**
     * 获取所有小区名用于精准查询
     * 搜索所有小区列表给添加更新模块使用
     * 5、redis类型hash,key: PREFIX + "names", hashkey: "list"
     */
    @Transactional
    public List<Community> selectAllCommunityName() {
        if (!redisTemplate.opsForHash().hasKey(PREFIX + "names", "list")) {
            LambdaQueryWrapper<Community> wrapper = new LambdaQueryWrapper();
            wrapper.select(Community::getCid, Community::getCname);
            List<Community> community_list = communityMapper.selectList(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "names", "list", community_list);
            redisTemplate.expire(PREFIX + "names", makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            return community_list;
        } else {
            List<Community> community_list = (List<Community>) redisTemplate.opsForHash()
                    .get(PREFIX + "names", "list");
            return community_list;
        }
    }

    /**
     * 统计所有小区楼栋数
     * @return 统计数
     * 6、redis类型string,key: "total_building"
     */
    @Transactional
    public BigDecimal countTotalBuildings() {
        if(! redisTemplate.hasKey("total_building")){
            BigDecimal sumCount = communityMapper.totalBuildings();
            redisTemplate.opsForValue().set("total_building",sumCount,makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("total_building从数据库统计");
            return sumCount;
        }else{
            Object totalBuildings = redisTemplate.opsForValue().get("total_building");
            BigDecimal sumCount = BigDecimal.valueOf(Long.parseLong(totalBuildings.toString()));
            log.info("total_building从redis拿");
            return sumCount;
        }
    }

    /**
     * 统计所有小区楼栋数
     * @return 统计数
     * 7、redis类型string,key: "total_house"
     */
    @Transactional
    public BigDecimal countTotalHouseholds() {
        if(! redisTemplate.hasKey("total_house")){
            BigDecimal sumCount = communityMapper.totalHouseholds();
            redisTemplate.opsForValue().set("total_house",sumCount,makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("total_house从数据库统计");
            return sumCount;
        }else{
            Object totalBuildings = redisTemplate.opsForValue().get("total_house");
            BigDecimal sumCount = BigDecimal.valueOf(Long.parseLong(totalBuildings.toString()));
            log.info("total_house从redis拿");
            return sumCount;
        }
    }

    //增删改操作

    /**
     * 添加小区数据
     */
    @Transactional
    public Integer insertCommunity(Community community) {
        //当更新小区数据时把楼栋那边的列表从redis缓存删除
        return communityMapper.insert(community);
    }

    /**
     * 更新小区数据
     */
    @Transactional
    public Integer updateCommunityById(Community community) {
        LambdaUpdateWrapper<Community> wrapper = new LambdaUpdateWrapper();
        wrapper.eq(Community::getCid,community.getCid());
        return communityMapper.update(community,wrapper);
    }

    /**
     * 根据id删除小区数据
     */
    @Transactional
    public Integer deleteCommunityById(Long id) {
        return communityMapper.deleteById(id);
    }
}
