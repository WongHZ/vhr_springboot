package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Repair;
import com.huan.vhr_springboot.mapper.RepairMapper;
import com.huan.vhr_springboot.service.RepairService;
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
public class RepairServiceimpl implements RepairService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "repair_";

    @Resource
    private RepairMapper repairMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查询全局数据列表
     * @param page 当前页数
     * @param cname if pid != 0 then 精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "cname_" + 当前登记人 + "_page_" + 当前页数
     */
    @Transactional
    public List<Repair> selectAllRepair(Long page, String cname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","cname_" + cname + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Repair> repairs_list = repairMapper.selectPage(pageNo,PAGESIZE,cname);
            redisTemplate.opsForHash().put(PREFIX + "page","cname_" + cname + "_page_" + page,repairs_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("cname为{}的repair页面数据从数据库拿",cname);
            return repairs_list;
        }else {
            List<Repair> repairs_list = (List<Repair>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","cname_" + cname + "_page_" + page);
            log.info("cname为{}的repair页面数据从redis拿",cname);
            return repairs_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if pid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else pid等于0 then 返回全部数据的数量
     * @param cname
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Integer total(String cname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","cname_" + cname)){
            Integer total;
            if(StrUtil.isNotBlank(cname)){
                total = repairMapper.countTotalByCname(cname);
            }else {
                total = repairMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","cname_" + cname,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("repair的总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","cname_" + cname);
            log.info("repair的总数据量从redis拿");
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
            log.info("repair的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("repair的总页数从redis拿");
            return pageNum;
        }
    }

    /**
     * 根据cid或cname查询数据
     * 前提为：cid=0L时用cname查询；cid != 0L时cid查询
     * 不能为双方都为null
     * @param rid
     * @param aname
     * 4、redis类型hash,key: PREFIX + "data", hashkey: "rid_" + 当前rid
     * 5、redis类型hash,key: PREFIX + "data", hashkey: "aname_" + 当前aname
     */
    @Transactional
    public Repair selectDataByridOrRname(Long rid, Long cid,String aname){
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper();
        wrapper.eq(rid != 0L,Repair::getRid,rid)
                .eq(cid != 0L,Repair::getCid,cid)
                .eq(StrUtil.isNotBlank(aname),Repair::getAname,aname)
                .select(Repair::getRid,Repair::getCid,Repair::getPid,Repair::getDid,
                        Repair::getAname,Repair::getDescr,Repair::getStatus,
                        Repair::getCreatetime,Repair::getUpdatetime,Repair::getIs_deleted);
        if(rid == 0L){
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","aname_" + aname)){
                Repair repair = repairMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","aname_" + aname,repair);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("aname为{}的数据从数据库拿",aname);
                return repair;
            }else {
                Repair repair = (Repair) redisTemplate.opsForHash()
                        .get(PREFIX + "data","aname_" + aname);
                log.info("aname为{}的数据从redis拿",aname);
                return repair;
            }
        }else{
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","rid_" + rid)){
                Repair repair = repairMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","rid_" + rid,repair);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("rid为{}的数据从数据库拿",rid);
                return repair;
            }else {
                Repair repair = (Repair) redisTemplate.opsForHash()
                        .get(PREFIX + "data","rid_" + rid);
                log.info("rid为{}的数据从redis拿",rid);
                return repair;
            }
        }
    }

    @Transactional
    public List<Repair> welData(){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "wel_list","data")){
            LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Repair::getRid,Repair::getDescr,Repair::getCreatetime,Repair::getStatus);
            List<Repair> repairList = repairMapper.selectList(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "wel_list","data",repairList);
            log.info("wel的维修数据从数据库拿");
            return repairList;
        }else {
            List<Repair> repairList = (List<Repair>)  redisTemplate.opsForHash()
                    .get(PREFIX + "wel_list","data");
            log.info("wel的维修数据从redis拿");
            return repairList;
        }
    }

    //增删改操作

    @Transactional
    public Integer changeStatus(Long id,Integer status){
        LambdaUpdateWrapper<Repair> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Repair::getRid,id).set(Repair::getStatus,status);
        Integer result = repairMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer addRepair(Repair repair){
        Integer result = repairMapper.insert(repair);
        return result;
    }
    @Transactional
    public Integer updateRepair(Repair repair){
        LambdaUpdateWrapper<Repair> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Repair::getRid,repair.getRid()).set(Repair::getCid,repair.getCid())
                .set(Repair::getPid,repair.getPid()).set(Repair::getDid,repair.getDid())
                .set(Repair::getDescr,repair.getDescr()).set(Repair::getUpdatetime,repair.getUpdatetime());
        Integer result = repairMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer delRepair(Long rid){
        Integer result = repairMapper.deleteById(rid);
        return result;
    }


}
