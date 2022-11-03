package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Paylist;
import com.huan.vhr_springboot.mapper.PaylistMapper;
import com.huan.vhr_springboot.service.PaylistService;
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
public class PaylistServiceimpl implements PaylistService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "paylist_";
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    PaylistMapper paylistMapper;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查询全局数据列表
     * @param page 当前页数
     * @param aname if pid != 0 then 精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "cname_" + 当前登记人 + "_page_" + 当前页数
     */
    @Transactional
    public List<Paylist> selectAllPaylist(Long page, String aname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","aname_" + aname + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Paylist> paylists_list = paylistMapper.selectPage(pageNo,PAGESIZE,aname);
            redisTemplate.opsForHash().put(PREFIX + "page","aname_" + aname + "_page_" + page,paylists_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("aname为{}的paylist页面数据从数据库拿",aname);
            return paylists_list;
        }else {
            List<Paylist> paylists_list = (List<Paylist>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","aname_" + aname + "_page_" + page);
            log.info("aname为{}的paylist页面数据从redis拿",aname);
            return paylists_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if pid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else pid等于0 then 返回全部数据的数量
     * @param aname
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Integer total(String aname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","aname_" + aname)){
            Integer total;
            if(StrUtil.isNotBlank(aname)){
                total = paylistMapper.countTotalByAname(aname);
            }else {
                total = paylistMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","aname_" + aname,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("paylist的总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","aname_" + aname);
            log.info("paylist的总数据量从redis拿");
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
            log.info("paylist的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("paylist的总页数从redis拿");
            return pageNum;
        }
    }

    @Transactional
    public Paylist selectDataByPaid(Long paid){
        LambdaQueryWrapper<Paylist> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Paylist::getPaid,Paylist::getCid,Paylist::getChid,Paylist::getPid,
                Paylist::getPay,Paylist::getDescr,Paylist::getAname,Paylist::getIs_deleted,
                Paylist::getCreatetime,Paylist::getUpdatetime).eq(Paylist::getPaid,paid);
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","paid_" + paid)){
            Paylist paylist = paylistMapper.selectOne(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "data","paid_" + paid,paylist);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("paid为{}的数据从数据库拿",paid);
            return paylist;
        }else {
            Paylist paylist = (Paylist) redisTemplate.opsForHash()
                    .get(PREFIX + "data","paid_" + paid);
            log.info("paid为{}的数据从redis拿",paid);
            return paylist;
        }
    }

    //增删改操作
    @Transactional
    public Integer addPaylist(Paylist paylist){
        Integer result = paylistMapper.insert(paylist);
        return result;
    }

    @Transactional
    public Integer upPaylist(Paylist paylist){
        LambdaUpdateWrapper<Paylist> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Paylist::getDescr,paylist.getDescr()).set(Paylist::getPay,paylist.getPay())
                .set(Paylist::getUpdatetime,paylist.getUpdatetime()).eq(Paylist::getPaid,paylist.getPaid());
        Integer result = paylistMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer delPaylist(Long paid){
        Integer result = paylistMapper.deleteById(paid);
        return result;
    }
}
