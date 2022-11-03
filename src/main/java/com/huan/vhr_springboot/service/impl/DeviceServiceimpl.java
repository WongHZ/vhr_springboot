package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Device;
import com.huan.vhr_springboot.mapper.DeviceMapper;
import com.huan.vhr_springboot.service.DeviceService;
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
public class DeviceServiceimpl implements DeviceService {
    private static final Long PAGESIZE = 6L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "device_";

    @Resource
    DeviceMapper deviceMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查询全局数据列表
     * @param page 当前页数
     * @param cid if pid != 0 then 精确查询
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "cid_" + 当前cid + "_page_" + 当前页数
     */
    @Transactional
    public List<Device> selectAllDevice(Long page, Long cid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","cid_" + cid + "_page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<Device> devices_list = deviceMapper.selectPage(pageNo,PAGESIZE,cid);
            redisTemplate.opsForHash().put(PREFIX + "page","cid_" + cid + "_page_" + page,devices_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("cid为{}的device页面数据从数据库拿",cid);
            return devices_list;
        }else {
            List<Device> devices_list = (List<Device>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","cid_" + cid + "_page_" + page);
            log.info("cid为{}的device页面数据从redis拿",cid);
            return devices_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if pid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else pid等于0 then 返回全部数据的数量
     * @param cid
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Integer total(Long cid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","cid_" + cid.toString())){
            Integer total;
            if(cid != 0 && cid != null){
                total = deviceMapper.countTotalByCid(cid);

            }else {
                total = deviceMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","cid_" + cid,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("device的总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","cid_" + cid);
            log.info("device的总数据量从redis拿");
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
            log.info("device的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("device的总页数从redis拿");
            return pageNum;
        }
    }

    /**
     * 根据did或dname查询数据
     * 前提为：did=0L时用dname查询；did != 0L时did查询
     * 不能为双方都为null
     * @param did
     * @param dname
     * 3、redis类型hash,key: PREFIX + "data", hashkey: "did_" + 当前did
     * 4、redis类型hash,key: PREFIX + "data", hashkey: "dname_" + 当前dname
     */
    @Transactional
    public Device selectDataBydidOrDname(Long did, Long cid,String dname){
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper();
        wrapper.eq(did != 0L,Device::getDid,did)
                .eq(cid != 0L,Device::getCid,cid)
                .eq(StrUtil.isNotBlank(dname),Device::getName,dname)
                .select(Device::getDid,Device::getCid,Device::getCode,Device::getName,
                        Device::getType,Device::getBrand,Device::getPrice,
                        Device::getBuynum, Device::getEulife,Device::getCreatetime,
                        Device::getUpdatetime,Device::getIs_deleted);
        if(did == 0L){
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","dname_" + dname)){
                Device device = deviceMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","dname_" + dname,device);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("dname为{}的数据从数据库拿",dname);
                return device;
            }else {
                Device device = (Device) redisTemplate.opsForHash()
                        .get(PREFIX + "data","dname_" + dname);
                log.info("dname为{}的数据从redis拿",dname);
                return device;
            }
        }else{
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","did_" + did)){
                Device device = deviceMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","did_" + did,device);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("did为{}的数据从数据库拿",did);
                return device;
            }else {
                Device device = (Device) redisTemplate.opsForHash()
                        .get(PREFIX + "data","did_" + did);
                log.info("did为{}的数据从redis拿",did);
                return device;
            }
        }
    }
    @Transactional
    public List<Device> selectAllDeviceName(Long cid){
        LambdaQueryWrapper<Device> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Device::getCid,cid).select(Device::getName);
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "_name","cid_" + cid)){
            List<Device> devices = deviceMapper.selectList(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "_name","cid_" + cid,devices);
            redisTemplate.expire(PREFIX + "_name",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("cid为{}的数据从数据库拿",cid);
            return devices;
        }else {
            List<Device> devices = (List<Device>) redisTemplate.opsForHash().get(PREFIX + "device_name","cid_" + cid);
            log.info("cid为{}的数据从redis拿",cid);
            return devices;
        }
    }

    //增删改操作

    /**
     * 新增数据
     * @param device
     * @return
     */
    @Transactional
    public Integer addDevice(Device device){
        Integer result = deviceMapper.insert(device);
        return result;
    }

    @Transactional
    public Integer upDevice(Device device,Integer page){
        LambdaUpdateWrapper<Device> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Device::getDid,device.getDid()).set(Device::getCid,device.getCid())
                .set(Device::getName,device.getName()).set(Device::getBrand,device.getBrand())
                .set(Device::getPrice,device.getPrice()).set(Device::getBuynum,device.getBuynum())
                .set(Device::getEulife,device.getEulife()).set(Device::getUpdatetime,device.getUpdatetime())
                .set(Device::getType,device.getType());
        Integer result = deviceMapper.update(null,wrapper);
        return result;
    }

    /**
     * 删除该did所属数据
     * @param did
     * @return
     */
    @Transactional
    public Integer delDevice(Long did){
        Integer result = deviceMapper.deleteById(did);
        return result;
    }
}
