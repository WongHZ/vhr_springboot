package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.ParkingUse;
import com.huan.vhr_springboot.mapper.ParkingUseMapper;
import com.huan.vhr_springboot.service.ParkingUseService;
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
public class ParkingUseServiceimpl implements ParkingUseService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "parker_";

    @Resource
    ParkingUseMapper parkingUseMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查询全局数据列表
     * @param page 当前页数
     * @param pid if pid != 0 then 精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "pid_" + 当前pid + "_page_" + 当前页数
     */
    @Transactional
    public List<ParkingUse> selectAllParkingUse(Long page, Long pid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","pid_" + pid + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<ParkingUse> parkingUses_list = parkingUseMapper.selectPage(pageNo,PAGESIZE,pid);
            redisTemplate.opsForHash().put(PREFIX + "page","pid_" + pid + "_page_" + page,parkingUses_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("pid为{}的parkinguse页面数据从数据库拿",pid);
            return parkingUses_list;
        }else {
            List<ParkingUse> parkingUses_list = (List<ParkingUse>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","pid_" + pid + "_page_" + page);
            log.info("pid为{}的parkinguse页面数据从redis拿",pid);
            return parkingUses_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if pid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else pid等于0 then 返回全部数据的数量
     * @param pid
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Integer total(Long pid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","pid_" + pid.toString())){
            Integer total;
            if(pid != 0 && pid != null){
                total = parkingUseMapper.countTotalByPid(pid);

            }else {
                total = parkingUseMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","pid_" + pid,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("parkinguse的总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","pid_" + pid);
            log.info("parkinguse的总数据量从redis拿");
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
            log.info("parkinguse的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("parkinguse的总页数从redis拿");
            return pageNum;
        }
    }

    /**
     * 根据车id查询id
     * @param carId
     * 4、redis类型hash,key: PREFIX + "id", hashkey: "cid_" + 当前car_id
     */
    @Transactional
    public Long selectIdBycarId(Long carId){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "id","cid_" + carId)){
            LambdaQueryWrapper<ParkingUse> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(ParkingUse::getId).eq(ParkingUse::getCarid,carId);
            ParkingUse parkingUse = parkingUseMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(parkingUse)){
                return 0L;
            }
            redisTemplate.opsForHash().put(PREFIX + "id","cid_" + carId,parkingUse.getId());
            redisTemplate.expire(PREFIX + "id",makeUtil.redisttl(MINTTL), TimeUnit.SECONDS);
            log.info("car_id为{}的parkinguse从数据库拿",parkingUse.getId());
            return parkingUse.getId();
        }else {
            Long id = (Long) redisTemplate.opsForHash().get(PREFIX + "id","cid_" + carId);
            log.info("car_id为{}的parkinguse从redis拿",id);
            return id;
        }
    }

    /**
     * 通过id查询数据,用于更新页面的表单数据
     *  不用redis
     * @param id 该条数据的id
     * @return
     */
    @Transactional
    public ParkingUse selectDataById(Long id){
        ParkingUse parkingUse = parkingUseMapper.selectDataById(id);
        return parkingUse;
    }

    //增删改操作

    /**
     * 新增车位使用数据
     * @param parkingUse 该数据的对象
     * @return
     */
    @Transactional
    public Integer addParkingUse(ParkingUse parkingUse){
        Integer result = parkingUseMapper.insert(parkingUse);
        return result;
    }

    /**
     * 更新数据
     * @param parkingUse
     * @return
     */
    @Transactional
    public Integer upParkingUse(ParkingUse parkingUse){
        LambdaUpdateWrapper<ParkingUse> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(ParkingUse::getCid,parkingUse.getCid()).set(ParkingUse::getParkingid,parkingUse.getParkingid())
                .set(ParkingUse::getCarid,parkingUse.getCarid()).set(ParkingUse::getPersonnelid,parkingUse.getPersonnelid())
                .set(ParkingUse::getUsetype,parkingUse.getUsetype()).set(ParkingUse::getTotalfee,parkingUse.getTotalfee())
                .set(ParkingUse::getStarttime,parkingUse.getStarttime()).set(ParkingUse::getEndtime,parkingUse.getEndtime())
                .set(ParkingUse::getUpdatetime,parkingUse.getUpdatetime()).eq(ParkingUse::getId,parkingUse.getId());
        Integer result = parkingUseMapper.update(null,wrapper);
        return result;
    }

    /**
     * 删除数据
     * @param id
     * @return
     */
    @Transactional
    public Integer parkingusedel(Long id){
        Integer result = parkingUseMapper.deleteById(id);
        return result;
    }
}
