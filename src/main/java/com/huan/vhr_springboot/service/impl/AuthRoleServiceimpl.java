package com.huan.vhr_springboot.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huan.vhr_springboot.entity.LoginAuthority;
import com.huan.vhr_springboot.mapper.LoginAuthorityMapper;
import com.huan.vhr_springboot.service.AuthRoleService;
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
@DS("login")
public class AuthRoleServiceimpl implements AuthRoleService {
    private static final Long PAGESIZE = 10L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 60;
    private static final String PREFIX = "authrole_";
    @Resource
    LoginAuthorityMapper loginAuthorityMapper;
    @Resource
    MakeUtil makeUtil;
    @Resource
    RedisTemplate redisTemplate;

    /**
     * 权限规则列表查询。
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "page_" + 当前页数
     */
    @Transactional
    public IPage<LoginAuthority> selectAllAuthRole(Long pageNo) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","page_" + pageNo)) {
            LambdaQueryWrapper<LoginAuthority> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(LoginAuthority::getAid, LoginAuthority::getAname, LoginAuthority::getUrl,
                            LoginAuthority::getType, LoginAuthority::getMethod, LoginAuthority::getDescription,
                            LoginAuthority::getStatus).gt(LoginAuthority::getType, 0)
                    .orderByDesc(LoginAuthority::getAname).orderByAsc(LoginAuthority::getMethod);
            IPage<LoginAuthority> authority_list = loginAuthorityMapper.selectPage(new Page<>(pageNo, PAGESIZE), wrapper);
            redisTemplate.opsForHash().put(PREFIX + "page","page_" + pageNo,authority_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("authrole页面数据从数据库拿");
            return authority_list;
        }else {
            IPage<LoginAuthority> authority_list = (IPage<LoginAuthority>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","page_" + pageNo);
            log.info("authrole页面数据从redis拿");
            return authority_list;
        }
    }

    /**
     * 判断目前角色是否已有该规则，
     *  if 有 then false, else true。
     * @param aname 角色名
     * @param url 模糊规则名
     * @return
     */
    @Transactional
    public boolean selectHasUrlByLikeUrlAndAname(String aname,String url){
        LambdaQueryWrapper<LoginAuthority> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LoginAuthority::getAid).eq(LoginAuthority::getAname, aname)
                .like(LoginAuthority::getUrl, url);
        List<LoginAuthority> authorityList = loginAuthorityMapper.selectList(wrapper);
        if (authorityList.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    //以下为增删改操作
    /**
     * 新增权限路径规则
     * 新增使用双删策略
     * 删除1号缓存和客制化springsecurity的权限表
     * @param loginAuthority
     * @return
     */
    @Transactional
    public Integer authorityadd(LoginAuthority loginAuthority){
        Integer result = loginAuthorityMapper.insert(loginAuthority);
        return result;
    }

    /**
     * 更改权限状态
     * 用双删策略
     * 删除1号缓存和客制化springsecurity的权限表
     * @param id
     * @param status
     * @param page
     * @return
     */
    @Transactional
    public Integer changeStatus(Long id,Integer status,Integer page){
        LambdaUpdateWrapper<LoginAuthority> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(LoginAuthority::getAid,id).set(LoginAuthority::getStatus,status);
        Integer result = loginAuthorityMapper.update(null,wrapper);
        return result;
    }

}
