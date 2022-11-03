package com.huan.vhr_springboot.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.mapper.LoginRoleMapper;
import com.huan.vhr_springboot.mapper.LoginUserMapper;
import com.huan.vhr_springboot.mapper.LoginUserRoleMapper;
import com.huan.vhr_springboot.service.AdminService;
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
@DS("login")
public class AdminServiceimpl implements AdminService {
    private static final Long PAGESIZE = 4L;
    private static final Integer DAYTTL = 86400;
    private static final Integer MINTTL = 180;
    private static final String PREFIX = "admin_";

    @Resource
    LoginUserMapper userMapper;
    @Resource
    LoginRoleMapper roleMapper;
    @Resource
    LoginUserRoleMapper userRoleMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    /**
     * 多表查询下的分页查询
     * @param page 当前页数
     * @param rid 如为0则搜索全部账号
     * @return
     * 1、redis类型hash,key: PREFIX + "page", hashkey: "page_" + 当前页数
     */
    @Transactional
    public List<LoginUser> selectAllLoginUser(Long page, Long rid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "page","page_" + page)){
            Long pageNo = (page-1)*PAGESIZE;
            List<LoginUser> loginUsers_list = userMapper.selectPage(pageNo,PAGESIZE,rid);
            redisTemplate.opsForHash().put(PREFIX + "page","page_" + page,loginUsers_list);
            redisTemplate.expire(PREFIX + "page",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("admin页面数据从数据库拿");
            return loginUsers_list;
        }else {
            List<LoginUser> loginUsers_list = (List<LoginUser>) redisTemplate.opsForHash()
                    .get(PREFIX + "page","page_" + page);
            log.info("admin页面数据从redis拿");
            return loginUsers_list;
        }
    }

    /**
     * 这里判断是否是精确查找
     * if rid 不为0 then 为精确查找，返回精确查找下共有几条数据
     *   else uid等于0 then 返回全部数据的数量
     * @param rid
     * @return
     * 2、redis类型hash,key: PREFIX + "total", hashkey: 当前rid
     */
    @Transactional
    public Integer total(Long rid) {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "total",rid.toString())){
            Integer total = 0;
            if(rid != 0 && rid != null){
                total = userMapper.countTotalByRid(rid);
            }else {
                total = userMapper.countTotal();
            }
            redisTemplate.opsForHash().put(PREFIX + "total",rid.toString(),total);
            redisTemplate.expire(PREFIX + "total",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("admin总数据量从数据库拿");
            return total;
        }else {
            Integer total = (Integer) redisTemplate.opsForHash().get(PREFIX + "total",rid.toString());
            log.info("admin总数据量从rediss拿");
            return total;
        }
    }

    /**
     * 计算共有几页，公式：页数=总数据量/每页显示量
     *  其中余数向上取整
     * @param total 总数据量
     * @return 页数
     * 3、redis类型string,key: PREFIX + "pagenum_" + total
     */
    @Transactional
    public Integer pageNum(Integer total) {
        if(! redisTemplate.hasKey(PREFIX + "pagenum")){
            double doubleNum = Math.ceil((double)total/PAGESIZE.intValue());
            Integer pageNum = (int)doubleNum;
            redisTemplate.opsForValue().set(PREFIX + "pagenum",pageNum);
            log.info("admin分页量从数据库拿");
            return pageNum;
        }else {
            Integer pageNum = (Integer) redisTemplate.opsForValue().get(PREFIX + "pagenum");
            log.info("admin分页量从redis拿");
            return pageNum;
        }
    }

    /**
     * 获取权限所属者名称，如超级管理员和普通管理员
     * @return
     * 4、redis类型hash,key: PREFIX + "role",hashkey: "roleName"
     */
    @Transactional
    public List<LoginRole> roleList(){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "role","roleName")){
            LambdaQueryWrapper<LoginRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(LoginRole::getRname,LoginRole::getRid);
            List<LoginRole> roleList = roleMapper.selectList(wrapper);
            if(roleList.isEmpty()){
                return null;
            }
            redisTemplate.opsForHash().put(PREFIX + "role","roleName",roleList);
            redisTemplate.expire(PREFIX + "role",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("角色列表从数据库拿");
            return roleList;
        }else {
            List<LoginRole> roleList = (List<LoginRole>) redisTemplate
                    .opsForHash().get(PREFIX + "role","roleName");
            log.info("角色列表从redis拿");
            return roleList;
        }
    }


    /**
     * 添加完用户表后还需要绑定角色表，因此通过
     * 手机来搜索该数据的id
     * @param phone
     * @return
     */
    @Transactional
    public Long selectUidByPhone(Long phone){
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LoginUser::getUid).eq(LoginUser::getPhone,phone);
        LoginUser user = userMapper.selectOne(wrapper);
        if(StrUtil.isBlankIfStr(user)){
            return 0L;
        }
        return user.getUid();
    }

    /**
     * 用于给页面判断，
     * if roleName == "超级管理员" then 前端显示管理员模块
     * @param username
     * @return
     * 5、redis类型hash,key: PREFIX + "role",hashkey: "username"
     */
    @Transactional
    public String selectRole(String username){
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "role",username)){
            LoginUser loginUser = userMapper.selectRole(username);
            String roleName = loginUser.getRole().getRname();
            log.info("fskdlfhsdj {} fjdksfjksdlf",username);
            redisTemplate.opsForHash().put(PREFIX + "role",username,roleName);
            redisTemplate.expire(PREFIX + "role",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("admin角色名从数据库拿");
            return roleName;
        }else {
            String roleName = (String) redisTemplate.opsForHash().get(PREFIX + "role",username);
            log.info("admin角色名从redis拿");
            return roleName;
        }
    }

    /**
     * 根据uid或name查询数据
     * @param uid
     * @param name
     * redis类型hash,key: PREFIX + "data",hashkey: "uanme"或"uid"
     */
    @Transactional
    public LoginUser selectDataByUidOrName(Long uid,String name){
        LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper();
        wrapper.eq(uid != 0L,LoginUser::getUid,uid)
                .eq(StrUtil.isNotBlank(name),LoginUser::getUname,name)
                .select(LoginUser::getUid,LoginUser::getUname,LoginUser::getEmail,
                        LoginUser::getPhone,LoginUser::getPassword,LoginUser::getImage,
                        LoginUser::getStatus);
        if(uid == 0L){
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","uname_" + name)){
                LoginUser loginUser = userMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","uname_" + name,loginUser);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("uname为{}的数据从数据库拿",name);
                return loginUser;
            }else {
                LoginUser loginUser = (LoginUser) redisTemplate.opsForHash()
                        .get(PREFIX + "data","name_" + name);
                log.info("uname为{}的数据从redis拿",name);
                return loginUser;
            }
        }else{
            if(! redisTemplate.opsForHash().hasKey(PREFIX + "data","uid_" + uid.toString())){
                LoginUser loginUser = userMapper.selectOne(wrapper);
                redisTemplate.opsForHash().put(PREFIX + "data","uid_" + uid,loginUser);
                redisTemplate.expire(PREFIX + "data",makeUtil.redisttl(DAYTTL), TimeUnit.SECONDS);
                log.info("uid为{}的数据从数据库拿",uid);
                return loginUser;
            }else {
                LoginUser loginUser = (LoginUser) redisTemplate.opsForHash()
                        .get(PREFIX + "data","uid_" + uid);
                log.info("uid为{}的数据从redis拿",uid);
                return loginUser;
            }
        }
    }

    /**
     * community模块
     * 管理者列表查询
     * 用于添加小区数据的负责人
     * 7、redis类型hash,key: PREFIX + "name",hashkey: "name_list"
     */
    @Transactional
    public List<LoginUser> selectAllUser() {
        if(! redisTemplate.opsForHash().hasKey(PREFIX + "name","name_list")){
            LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper();
            wrapper.select(LoginUser::getUid,LoginUser::getUname).eq(LoginUser::getStatus,1);
            List<LoginUser> admin_list = userMapper.selectList(wrapper);
            redisTemplate.opsForHash().put(PREFIX + "name","name_list",admin_list);
            redisTemplate.expire(PREFIX + "name",makeUtil.redisttl(DAYTTL),TimeUnit.SECONDS);
            log.info("admin用户名列表从数据库拿");
            return admin_list;
        }else {
            List<LoginUser> admin_list = (List<LoginUser>) redisTemplate.opsForHash().get(PREFIX + "name","name_list");
            log.info("admin用户名列表从redis拿");
            return admin_list;
        }
    }

    //以下为增删改操作
    /**
     * 修改该条数据的状态
     * @param id
     * @param status
     * @return
     * 双删策略
     * 删除7号redis缓存和该条数据所在页面的缓存
     */
    @Transactional
    public Integer changeStatus(Long id,Integer status,Integer page){
        LambdaUpdateWrapper<LoginUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(LoginUser::getStatus,status).eq(LoginUser::getUid,id);
        Integer result = userMapper.update(null,wrapper);
        return result;
    }

    /**
     * 采用双删策略
     * @param loginUser
     * @return
     * 添加管理员后可能导致页面数据发生变化
     * 因此直接删除整个1号缓存
     * 双删策略，删除1、2、3、7号缓存
     */
    @Transactional
    public Integer adminadd(LoginUser loginUser){
        Integer result = userMapper.insert(loginUser);
        return result;
    }

    /**
     * 把新管理员和角色表绑定
     * @param loginUserRole
     * @return
     */
    @Transactional
    public Integer userRoleAdd(LoginUserRole loginUserRole){
        Integer result = userRoleMapper.insert(loginUserRole);
        return result;
    }

    /**
     * 删除该条账号数据，不是逻辑删除
     * @param id uid
     * @return
     */
    @Transactional
    public Integer admindel(Long id,Integer page){
        Integer result = userMapper.deleteById(id);
        return result;
    }

    /**
     * 删除绑定该uid和rid的数据
     * @param id
     * @return
     */
    @Transactional
    public Integer adminRoledel(Long id){
        LambdaQueryWrapper<LoginUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginUserRole::getUid,id);
        Integer result = userRoleMapper.delete(wrapper);
        return result;
    }

    @Transactional
    public Integer updateUser(LoginUser user){
        LambdaUpdateWrapper<LoginUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(LoginUser::getEmail,user.getEmail()).set(LoginUser::getPhone,user.getPhone())
                .set(StrUtil.isNotBlank(user.getPassword()),LoginUser::getPassword,user.getPassword())
                .eq(LoginUser::getUid,user.getUid());
        Integer result = userMapper.update(null,wrapper);
        return result;
    }

    @Transactional
    public Integer updatePass(Long uid,String npass){
        LambdaUpdateWrapper<LoginUser> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(LoginUser::getPassword,npass).eq(LoginUser::getUid,uid);
        Integer result = userMapper.update(null,wrapper);
        return result;
    }
}
