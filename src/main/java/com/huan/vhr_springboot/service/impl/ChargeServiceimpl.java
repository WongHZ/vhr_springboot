package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Charge;
import com.huan.vhr_springboot.mapper.ChargeMapper;
import com.huan.vhr_springboot.service.ChargeService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChargeServiceimpl implements ChargeService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "charge_";

    @Resource
    private ChargeMapper chargeMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查询全局数据列表
     * @param page 当前页数
     * @param chid if pid != 0 then 精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "chid_" + 当前数据id + "_page_" + 当前页数
     */
    @Transactional
    public List<Charge> selectAllCharge(Long page, Long chid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","chid_" + chid + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Charge> charges_list = chargeMapper.selectPage(pageNo,PAGESIZE,chid);
            redisTemplate.opsForHash().put(PREFIX + "page","chid_" + chid + "_page_" + page,charges_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("chid为{}的charge页面数据从数据库拿",chid);
            return charges_list;
        }else {
            List<Charge> charges_list = (List<Charge>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","chid_" + chid + "_page_" + page);
            log.info("chid为{}的charge页面数据从redis拿",chid);
            return charges_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if pid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else pid等于0 then 返回全部数据的数量
     * @param chid
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Integer total(Long chid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","chid_" + chid)){
            Integer total;
            if(chid != 0L){
                total = chargeMapper.countTotalByChid(chid);
            }else {
                total = chargeMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","chid_" + chid,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("charge的总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","chid_" + chid);
            log.info("charge的总数据量从redis拿");
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
            log.info("charge的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("charge的总页数从redis拿");
            return pageNum;
        }
    }

    @Transactional
    public Charge selectDataByChidOrCode(Long chid,String code,String name) {
        LambdaQueryWrapper<Charge> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Charge::getChid, Charge::getCid, Charge::getName, Charge::getCode,
                        Charge::getPrice, Charge::getCreatetime, Charge::getUpdatetime, Charge::getIs_deleted)
                .eq(chid != 0L, Charge::getChid, chid)
                .eq(StrUtil.isNotBlank(code), Charge::getCode, code)
                .eq(StrUtil.isNotBlank(name), Charge::getName,name);
        if (StrUtil.isNotBlank(code)) {
            if (!redisTemplate.opsForHash().hasKey(PREFIX + "data", "code_" + code)) {
                Charge charge = chargeMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data", "code_" + code, charge);
                redisTemplate.expire(PREFIX + "data", makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("charge的搜索数据从数据库拿");
                return charge;
            } else {
                Charge charge = (Charge) redisTemplate.opsForHash()
                        .get(PREFIX + "data", "code_" + code);
                log.info("charge的搜索数据从redis拿");
                return charge;
            }
        } else if(chid != 0L){
            if (!redisTemplate.opsForHash().hasKey(PREFIX + "data", "chid_" + chid)) {
                Charge charge = chargeMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data", "chid_" + chid, charge);
                redisTemplate.expire(PREFIX + "data", makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("charge的搜索数据从数据库拿");
                return charge;
            } else {
                Charge charge = (Charge) redisTemplate.opsForHash()
                        .get(PREFIX + "data", "chid_" + chid);
                log.info("charge的搜索数据从redis拿");
                return charge;
            }
        } else {
            if (!redisTemplate.opsForHash().hasKey(PREFIX + "data", "name_" + name)) {
                Charge charge = chargeMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data", "name_" + name, charge);
                redisTemplate.expire(PREFIX + "data", makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("charge的搜索数据从数据库拿");
                return charge;
            } else {
                Charge charge = (Charge) redisTemplate.opsForHash()
                        .get(PREFIX + "data", "name_" + name);
                log.info("charge的搜索数据从redis拿");
                return charge;
            }
        }
    }

    @Transactional
    public List<Charge> selectDataByCid(Long cid){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","cid_" + cid)){
            LambdaQueryWrapper<Charge> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Charge::getChid,Charge::getName).eq(Charge::getCid,cid);
            List<Charge> charges = chargeMapper.selectList(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "data","cid_" + cid,charges);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("cid为{}的数据从数据库拿",cid);
            return charges;
        }else {
            List<Charge> charges = (List<Charge>) redisTemplate.opsForHash()
                    .get(PREFIX + "data","cid_" + cid);
            log.info("cid为{}的数据从redis拿",cid);
            return charges;
        }
    }

    //增删改操作
    @Transactional
    public Integer addCharge(Charge charge){
        Integer result = chargeMapper.insert(charge);
        return result;
    }

    @Transactional
    public Integer upCharge(Charge charge){
        LambdaUpdateWrapper<Charge> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Charge::getName,charge.getName()).set(Charge::getCode,charge.getCode())
                .set(Charge::getPrice,charge.getPrice()).set(Charge::getUpdatetime,charge.getUpdatetime())
                .eq(Charge::getChid,charge.getChid());
        Integer result = chargeMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer delCharge(Long chid){
        Integer result = chargeMapper.deleteById(chid);
        return result;
    }
}
