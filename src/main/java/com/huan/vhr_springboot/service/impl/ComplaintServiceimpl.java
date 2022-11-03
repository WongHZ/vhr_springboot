package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Complaint;
import com.huan.vhr_springboot.mapper.ComplaintMapper;
import com.huan.vhr_springboot.service.ComplaintService;
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
public class ComplaintServiceimpl implements ComplaintService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "complaint_";

    @Resource
    private ComplaintMapper complaintMapper;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    MakeUtil makeUtil;

    /**
     * 查询全局数据列表
     * @param page 当前页数
     * @param aname if pid != 0 then 精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "aname_" + 当前登记人 + "_page_" + 当前页数
     */
    @Transactional
    public List<Complaint> selectAllComplaint(Long page, String aname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","aname_" + aname + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Complaint> complaints_list = complaintMapper.selectPage(pageNo,PAGESIZE,aname);
            redisTemplate.opsForHash().put(PREFIX + "page","aname_" + aname + "_page_" + page,complaints_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("aname为{}的complaint页面数据从数据库拿",aname);
            return complaints_list;
        }else {
            List<Complaint> complaints_list = (List<Complaint>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","aname_" + aname + "_page_" + page);
            log.info("aname为{}的complaint页面数据从redis拿",aname);
            return complaints_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if pid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else pid等于0 then 返回全部数据的数量
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Integer total(String aname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","aname_" + aname)){
            Integer total;
            if(StrUtil.isNotBlank(aname)){
                total = complaintMapper.countTotalByAname(aname);
            }else {
                total = complaintMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","aname_" + aname,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("complaint的总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","aname_" + aname);
            log.info("complaint的总数据量从redis拿");
            return total;
        }
    }

    @Transactional
    public Integer totalNew() {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","new")){
            Integer total = complaintMapper.countTotalNew();
            redisTemplate.opsForHash().put(PREFIX + "total","new",total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("complaint的最新总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","new");
            log.info("complaint的最新总数据量从redis拿");
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
            log.info("complaint的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("complaint的总页数从redis拿");
            return pageNum;
        }
    }

    @Transactional
    public Complaint selectDataByCid(Long cpid){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","cpid_" + cpid)){
            LambdaQueryWrapper<Complaint> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Complaint::getCpid,Complaint::getCid,Complaint::getPid,Complaint::getDescr,
                            Complaint::getStatus,Complaint::getAname,Complaint::getCreatetime,Complaint::getUpdatetime)
                    .eq(Complaint::getCpid,cpid);
            Complaint complaint = complaintMapper.selectOne(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "data","cpid_" + cpid,complaint);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("cpid为{}的数据从数据库拿",cpid);
            return complaint;
        }else {
            Complaint complaint = (Complaint) redisTemplate.opsForHash()
                    .get(PREFIX + "data","cpid_" + cpid);
            log.info("cpid为{}的数据从redis拿",cpid);
            return complaint;
        }
    }

    //增删改操作

    @Transactional
    public Integer addComplaint(Complaint complaint){
        Integer result = complaintMapper.insert(complaint);
        return result;
    }

    @Transactional
    public Integer changeStatus(Long cpid,Integer status){
        LambdaUpdateWrapper<Complaint> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Complaint::getCpid,cpid).set(Complaint::getStatus,status);
        Integer result = complaintMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer upComplaint(Complaint complaint){
        LambdaUpdateWrapper<Complaint> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(Complaint::getCid,complaint.getCid()).set(Complaint::getDescr,complaint.getDescr())
                .set(Complaint::getUpdatetime,complaint.getUpdatetime())
                .eq(Complaint::getCpid,complaint.getCpid());
        Integer result = complaintMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer delComplaint(Long cpid){
        Integer result = complaintMapper.deleteById(cpid);
        return result;
    }
}
