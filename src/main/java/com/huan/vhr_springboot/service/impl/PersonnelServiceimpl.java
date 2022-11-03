package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.Personnel;
import com.huan.vhr_springboot.mapper.PersonnelMapper;
import com.huan.vhr_springboot.service.PersonnelService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PersonnelServiceimpl implements PersonnelService {
    private static final Long PAGESIZE = 4L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "persons_";

    @Resource
    PersonnelMapper personnelMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 因需要多表查询，使用join语句，所以不能直接使用plus
     * 这里通过自定义的mapper.xml去实现具有精确查找的分页功能
     * @param page 当前页面
     * @param houseId 房产名的id
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "hid_" + 当前hid + "_page_" + 当前页数
     */
    @Transactional
    public List<Personnel> selectAllPersonnel(Long page, Long houseId) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","hid_" + houseId + "_page_" + page)) {
            Long pageNo = (page - 1) * PAGESIZE;
            List<Personnel> personnels_list = personnelMapper.selectPage(pageNo, PAGESIZE, houseId);
            redisTemplate.opsForHash().put(PREFIX + "page","hid_" + houseId + "_page_" + page,personnels_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("personnel的页面数据从数据库拿");
            return personnels_list;
        }else {
            List<Personnel> personnels_list = (List<Personnel>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","hid_" + houseId + "_page_" + page);
            log.info("personnel的页面数据从redis拿");
            return personnels_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if houseid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else houseid等于0 then 返回全部数据的数量
     * 2、redis类型hash,key: PREFIX + "total", hashkey: "hid_" + 当前hid
     */
    @Transactional
    public Integer total(Long houseId) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total","hid_" + houseId.toString())) {
            Integer total;
            if (houseId != 0 && houseId != null) {
                total = personnelMapper.countTotalByHid(houseId);
            } else {
                total = personnelMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total","hid_" + houseId,total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("personnel的总数量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "total","hid_" + houseId);
            log.info("personnel的总数量从redis拿");
            return total;
        }
    }

    /**
     * 计算共有几页，公式：页数=总数据量/每页显示量
     *  其中余数向上取整
     * @param total 总数据量
     * @return 页数
     * 3、redis类型hash,key: PREFIX + "pagenum", hashkey: "total_" + 总数据量
     */
    @Transactional
    public Integer pageNum(Integer total) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "pagenum","total_" + total.toString())){
            double doubleNum = Math.ceil((double)total/PAGESIZE.intValue());
            Integer pageNum = (int)doubleNum;
            redisTemplate.opsForHash().put(PREFIX + "pagenum","total_" + total,pageNum);
            redisTemplate.expire(PREFIX + "pagenum",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("personnel的总页数从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForHash()
                    .get(PREFIX + "pagenum","total_" + total);
            log.info("personnel的总页数从redis拿");
            return pageNum;
        }
    }

    /**
     * 用于添加小区数据时判断
     * if 添加的小区名字已存在数据库 then 返回此名字
     * else 返回空字符串
     * @param name 新增输入的小区名字
     * @return
     */
    @Transactional
    public Personnel selectDataByName(String name){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "temp_name","pname_" + name)){
            LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Personnel::getPname,name).select(Personnel::getPname,Personnel::getPhone,Personnel::getPid);
            Personnel personnel = personnelMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(personnel)){
                redisTemplate.opsForHash().put(PREFIX + "temp_name","pname_" + name,null);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(MINTTL),TimeUnit.SECONDS);
                log.info("名字为{}的数据从数据库拿",name);
                return null;
            }else {
                redisTemplate.opsForHash().put(PREFIX + "temp_name","pname_" + name,personnel);
                log.info("名字为{}的数据从数据库拿",name);
                return personnel;
            }
        }else {
            Personnel personnel = (Personnel) redisTemplate.opsForHash().get(PREFIX + "temp_name","pname_" + name);
            log.info("名字为{}的数据从redis拿",name);
            return personnel;
        }
    }

    /**
     * 根据成员数据id查询该条数据
     * @param pid
     * 4、redis类型hash,key: PREFIX + "data", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public Personnel selectByPid(Long pid){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","pid_" + pid)){
            Personnel personnels = personnelMapper.selectBypId(pid);
            redisTemplate.opsForHash().put(PREFIX + "data","pid_" + pid,personnels);
            redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("pid为{}的数据从数据库拿");
            return personnels;
        }else {
            Personnel personnels = (Personnel) redisTemplate.opsForHash()
                    .get(PREFIX + "data","pid_" + pid);
            log.info("pid为{}的数据从redis拿");
            return personnels;
        }
    }

    /**
     * welcome页面统计租客用
     * 5、redis类型string,key: PREFIX + "wel_tenant"
     */
    @Transactional
    public Integer totalTenant(){
        if(! redisTemplate.hasKey(PREFIX + "wel_tenant")){
            Integer result = personnelMapper.countTotalTenant();
            redisTemplate.opsForValue().set(PREFIX + "wel_tenant",result,makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
            log.info("welcome页面统计租客从数据库拿");
            return result;
        }else {
            Object object = redisTemplate.opsForValue().get(PREFIX + "wel_tenant");
            log.info("welcome页面统计租客从redis拿");
            return Integer.parseInt(object.toString());
        }
    }

    /**
     * 接口传来管理员对应的oid
     * 根据pid返回业主姓名。
     * 6、redis类型hash,key: PREFIX + "pname", hashkey: "pid_" + 当前pid
     */
    @Transactional
    public String selectOwnerNameById(Long pid) {
        if(pid != 0L){
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "pname", "pid_"+ pid)){
                LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper();
                wrapper.select(Personnel::getPname).eq(Personnel::getPid,pid);
                Personnel personnel = personnelMapper.selectOne(wrapper);
                String name = personnel.getPname();
                redisTemplate.opsForHash().put(PREFIX + "pname", "pid_"+ pid,name);
                redisTemplate.expire(PREFIX + "pname",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("pid为{}的业主姓名从数据库拿",pid);
            }else {
                String name = (String) redisTemplate.opsForHash().get(PREFIX + "pname", "pid_"+ pid);
                log.info("pid为{}的业主姓名从redis拿",pid);
                return name;
            }
        }
        return null;
    }

    /**
     * 根据业主姓名查询业主id
     * @param pname 业主姓名
     * @return 业主id
     * 7、redis类型hash,key: PREFIX + "pname", hashkey: "pname_" + 当前业主姓名
     */
    @Transactional
    @Cacheable("pidName")
    public Long selectOidByOname(String pname) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "pname", "pname_"+ pname)){
            LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Personnel::getPid).eq(Personnel::getPname,pname);
            Personnel personnel = personnelMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(personnel)){
                redisTemplate.opsForHash().put(PREFIX + "pname", "pname_"+ pname,0L);
                redisTemplate.expire(PREFIX + "pname",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
                log.info("pname为{}的业主姓名从数据库拿",pname);
                return 0L;
            }else {
                redisTemplate.opsForHash().put(PREFIX + "pname", "pname_"+ pname,personnel.getPid());
                redisTemplate.expire(PREFIX + "pname",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
                log.info("pname为{}的业主姓名从数据库拿",pname);
                return personnel.getPid();
            }
        }else {
            Long pid = (Long) redisTemplate.opsForHash()
                    .get(PREFIX + "pname", "pname_"+ pname);
            log.info("pname为{}的业主姓名从redis拿",pname);
            return pid;
        }
    }

    //增删改操作

    /**
     * 新增成员数据
     * @param personnel
     * @return
     */
    @Transactional
    public Integer personneladd(Personnel personnel){
        Integer result = personnelMapper.insert(personnel);
        return result;
    }

    /**
     * 更新成员数据
     * @param personnel
     * @return
     */
    @Transactional
    public Integer personnelupdate(Personnel personnel){
        LambdaUpdateWrapper<Personnel> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Personnel::getPid,personnel.getPid()).set(Personnel::getCid,personnel.getCid())
                .set(Personnel::getHid,personnel.getHid()).set(Personnel::getPname,personnel.getPname())
                .set(Personnel::getImage,personnel.getImage()).set(Personnel::getIdCard,personnel.getIdCard())
                .set(Personnel::getPhone,personnel.getPhone()).set(Personnel::getSex,personnel.getSex())
                .set(Personnel::getBirthday,personnel.getBirthday()).set(Personnel::getType,personnel.getType())
                .set(Personnel::getUpdatetime,personnel.getUpdatetime());
        Integer result = personnelMapper.update(null,wrapper);
        return result;
    }

    /**
     * 删除成员信息
     * @param pid
     * @return
     */
    @Transactional
    public Integer personneldel(Long pid){
        Integer result = personnelMapper.deleteById(pid);
        return result;
    }
}
