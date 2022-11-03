package com.huan.vhr_springboot;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.mapper.*;
import com.huan.vhr_springboot.service.*;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Float.NaN;

@SpringBootTest
@Slf4j
public class projectTest {
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    CommunityMapper communityMapper;
    @Resource
    ParkingUseService parkingUseService;
    @Resource
    HouseService houseService;
    @Resource
    LoginUserMapper userMapper;
    @Resource
    CommunityService communityService;
    @Resource
    DeviceService deviceService;
    @Resource
    HouseMapper houseMapper;
    @Resource
    PersonnelMapper personnelMapper;
    @Resource
    CarMapper carMapper;
    @Resource
    LoginUserMapper loginUserMapper;
    @Resource
    ParkingMapper parkingMapper;
    @Resource
    LoginAuthorityMapper loginAuthorityMapper;
    @Resource
    MakeUtil makeUtil;
    @Test
    @DisplayName("获取初次账号的密码加密")
    public void loadUserByUsername(){
        String pass = passwordEncoder.encode("111");
        System.out.println(pass);
        boolean match = passwordEncoder.matches("111",pass);
        System.out.println(match);
    }

    @Test
    @DisplayName("list循环")
    public void looplist(){
        int p = 5;
        List<Integer> list = makeUtil.turnlist(5);
        list.forEach(System.out::println);
    }

    @Test
    @DisplayName("redis获取用户名")
    public void getUserNameByRedis(){
        LoginUser user = (LoginUser) redisTemplate.opsForHash().get("login_user","huan");
        System.out.println(user.getUname());
    }

    @Test
    @DisplayName("更新图片名称")
    public void fileName(){
        String oname = "afaf.jpg";
        String newName = oname.substring(0,oname.lastIndexOf(".")) +
                UUID.randomUUID().toString() + oname.substring(oname.lastIndexOf("."));
        System.out.println(newName);
    }

    @Test
    @DisplayName("统计所有小区楼栋数")
    public void countTotalBuildings() {
        BigDecimal sumCount =  communityMapper.totalBuildings();
        System.out.println(sumCount);
    }

    @Test
    @DisplayName("房产资料更改")
    public void updateHouse() {
        String livetime = "2022.07.23";
        Date date = makeUtil.turnDate(livetime);
        House house = new House(1L,1L,"光大锦绣山河","A栋","HM20191213003","光大锦绣山河A栋9层3单元",
                1L,"王兆桓",13169353122L,4,3,9,date);
        LambdaUpdateWrapper<House> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(House::getHId,house.getHId()).set(House::getCId,house.getCId()).set(House::getCName,house.getCName())
                .set(House::getBName,house.getBName()).set(House::getHCode,house.getHCode()).set(House::getHName,house.getHName())
                .set(House::getOId,house.getOId()).set(House::getOName,house.getOName()).set(House::getOPhone,house.getOPhone())
                .set(House::getRoomNum,house.getRoomNum()).set(House::getUnit,house.getUnit()).set(House::getFloor,house.getFloor())
                .set(House::getLiveTime,house.getLiveTime());
        Integer result = houseMapper.update(null,wrapper);
    }

    @Test
    @DisplayName("根据oid查询业主名字")
    public void selectOidByName() {
        String ownerName = "王兆桓";
        LambdaQueryWrapper<Personnel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Personnel::getPname,ownerName).select(Personnel::getPid);
        Personnel personnel = personnelMapper.selectOne(wrapper);
        System.out.println(personnel.getPid());
    }

    @Test
    @DisplayName("查询redis的key是否存在")
    public void keyexist() {
        if(! redisTemplate.hasKey("manager_uid")){
            System.out.println("1");
        }else if(redisTemplate.hasKey("manager_uid")){
            System.out.println("2");
        }else {
            System.out.println("3");
        }
    }

    @Test
    @DisplayName("成员的多表查询plusIpage的功能")
    public void sexEnum() {
        Personnel personnel = new Personnel();
        personnel.getSex().getSexName();
        List<Personnel> list = personnelMapper.selectPage((1-1)*5L,5L,null);
        list.forEach(System.out::println);
    }

    @Test
    @DisplayName("页数向上取整")
    public void pageNum() {
        Integer a = 8;
        Integer b = 6;
        double dnum = Math.ceil((double)a/b);
        Integer num = (int)dnum;
        System.out.println(Math.ceil(8.00/6.00) + "--" + dnum + "--" + num);
    }

    @Test
    @DisplayName("url编码")
    public void urlCode() {
        String name = URLDecoder.decode("我的",
                StandardCharsets.UTF_8);
        if(name.equals("0")){
            System.out.println("hello!!");
        }
            System.out.println(name);
    }

    @Test
    @DisplayName("redis存取list")
    public void redisgetMap() {
        List<LoginAuthority> map = (List<LoginAuthority>) redisTemplate.opsForHash().get("authority","uid_1");
        map.forEach(System.out::println);
    }

    @Test
    @DisplayName("根据车辆id获取数据")
    public void carData(){
        Long cid = 1L;
        if(StrUtil.isBlankIfStr(redisTemplate.opsForHash().get("car_cid_" + cid,"car_" + cid))){
            Car car = carMapper.selectDataById(cid);
            redisTemplate.opsForHash().put("car_cid_" + cid,"car_" + cid,car);
            redisTemplate.expire("car_cid_" + cid,makeUtil.redisttl(180), TimeUnit.SECONDS);
            System.out.println("从数据库拿：" + car.toString());
        }else {
            Car car =  (Car) redisTemplate.opsForHash().get("car_cid_" + cid,"car_" + cid);
            System.out.println("从redis拿：" + car.toString());
        }
    }

    @Test
    public void a(){
        Long id = 1L;
        ParkingUse parkingUse = parkingUseService.selectDataById(id);
        System.out.println(parkingUse.toString());
    }

    @Test
    @DisplayName("resultmap两个实体类")
    public void resultmapForTwoEntity(){
        List<LoginUser> list = loginUserMapper.selectPage(0L,6L,0L);
        list.forEach(System.out::println);
        for(LoginUser i : list){
            System.out.println(i.getRole().getRname());
        }
    }

    @Test
    @DisplayName("redis存放")
    public void selectParkingByparkingcode(){
        String parkingCode = "HXA006";
        if(! redisTemplate.opsForHash().hasKey("parking","code")){
            LambdaQueryWrapper<Parking> wrapper = new LambdaQueryWrapper<>();
            wrapper.select(Parking::getId).eq(Parking::getCode,parkingCode);
            Parking parking = parkingMapper.selectOne(wrapper);
            if(StrUtil.isBlankIfStr(parking)){
                System.out.println(0);
            }
            redisTemplate.opsForHash().put("parking","code",parking.getId());
            redisTemplate.expire("parking",180,TimeUnit.SECONDS);
            System.out.println(parking.getId());
        }else {
            Long aLong = (Long) redisTemplate.opsForHash().get("parking","code");
            System.out.println(aLong);
        }
    }

    @Test
    @DisplayName("字符串处理")
    public void testToString(){
        String parkingCode = "cdadd";
        System.out.println(parkingCode.endsWith("add"));
        String url2 = parkingCode.substring(0,parkingCode.lastIndexOf("add"));
        url2 = "/" + url2;
        System.out.println(url2);
    }

    @Test
    @DisplayName("返回boolean值")
    public void boolTest(){
        LambdaQueryWrapper<LoginAuthority> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LoginAuthority::getAid).eq(LoginAuthority::getAname,"admin")
                .like(LoginAuthority::getUrl,"comm");
        LoginAuthority authority = loginAuthorityMapper.selectOne(wrapper);
        if(StrUtil.isBlankIfStr(authority)){
            System.out.println(true);
        }else{
            System.out.println(false);
        }
    }

    @Test
    @DisplayName("String和StringBuilder")
    public void StringTest(){

        String url = "communityupdate";
        String url2 = "/";
        Long stime = System.nanoTime();//用StringBuilder耗时十万毫秒
        if(url.endsWith("add")){
            url2 = url + "Data";
        }else if(url.endsWith("update")){
            url2 = url.substring(0,url.lastIndexOf("update")) + "upData";
        }
        Long etime = System.nanoTime();
        System.out.println("12:" + (etime - stime) + "ns");

        StringBuilder url22 = new StringBuilder("/");
        Long stime1 = System.nanoTime();//用StringBuilder耗时万毫秒
        if(url.endsWith("add")){
            url22.append(url).append("Data");
        }else if(url.endsWith("update")){
            url22.append(url, 0, url.lastIndexOf("update")).append("upData");
        }
        Long etime1 = System.nanoTime();
        System.out.println(url22);
        System.out.println("22:" + (etime1 - stime1) + "ns");
    }

    @Test
    @DisplayName("redis批量删除")
    public void redisdelTest(){
        String PREFIX = "admin_";
        Collection<String> collection = new ArrayList<>();
        collection.add(PREFIX + "page");
        collection.add(PREFIX + "total");
        collection.add(PREFIX + "pagenum");
        collection.add(PREFIX + "name");
        Long result = redisTemplate.delete(collection);
        System.out.println(result);
    }

    @Test
    @DS("login")
    @DisplayName("操作数据库所需时间")
    public void changeStatusTimeTest(){
        //更新一个字段大概在0.48秒左右
        //新增数据库大概在0.5~0.67秒左右
        //删除三条数据大概在0.47~0.5秒左右
        /*LambdaQueryWrapper<LoginUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(LoginUser::getUname,"test");
        Long stime = System.nanoTime();
        userMapper.delete(wrapper);
        Long etime = System.nanoTime();
        System.out.println("12:" + (etime - stime) + "ns");*/
        Collection<String> collection = new ArrayList<>();
        collection.add("admin_total");
        collection.add("admin_pagenum");
        collection.add("admin_name");
        redisTemplate.delete(collection);
        redisTemplate.opsForHash().delete("admin_id","8");
        redisTemplate.opsForHash().delete("admin_page","page_" + 1);
    }

    @Test
    @DS("login")
    @DisplayName("cid或cname返回数据")
    public void selectBycidOrcnameTest(){
        Community community = communityService.selectDataByCidOrCname(1L,null);
        System.out.println(community.toString());
    }

    @Test
    @DS("login")
    @DisplayName("获取时间戳年月日")
    public void getTimeTest(){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp.toString().substring(0,10).replace("-",""));
        String a = "VH47238947dfh";
        String b = "A栋";
        String c = "ELE";
        StringBuilder code = new StringBuilder();
        code.append(a.substring(0,2)).append(b.substring(0,1))
                .append(c).append(timestamp.toString().substring(0,10).replace("-",""));
        System.out.println(code.toString());
        Random random = new Random();
        System.out.println(random.nextInt(100));
        BigDecimal price = new BigDecimal("20.00");
        System.out.println(price);
        String d = "BHBFFSAF";
        String build = d.substring(2,3);
        System.out.println(build);
    }


    @Test
    @DS("login")
    @DisplayName("获取时间戳年月日")
    public void geTimeTest(){
        List<Device> devices = deviceService.selectAllDeviceName(1L);
        String[] name = new String[devices.size()];
        for(int i = 0; i < name.length;i++){
            name[i] = devices.get(i).getName();
            System.out.println(name[i]);
        }
    }

    @Test
    @DS("login")
    @DisplayName("获取时间戳年月日")
    public void subTest(){
        String head = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJsb2dpbl90aW1lIjoxNjY2NDMwOTYwLCJ1c2VybmFtZSI6IueOi-WFiOajriJ9.zeS_pZQkdC1Ew0bTj1aYEFBahBa48paJGafYvfiwHbmGQk5ouG5_UnoUB1_jz-T0xhG4kEGa5a4c4Rndn3zJQg";
       /* StringBuilder builder = new StringBuilder(head);
        builder.append(head.substring(head.indexOf("Authorization=")))
                .replace(0,13,"").delete(0,8);*/
        JWT jwt = JWTUtil.parseToken(head);
        String username = (String) jwt.getPayload().getClaim("username");
        System.out.println(username);
    }

    @Test
    @DisplayName("获取类加载器")
    public void classLoaderTest(){
        try {
            //1.获取引导类加载器
            ClassLoader classLoader = Class.forName("java.lang.ClassLoader").getClassLoader();
            System.out.println(classLoader);
            //2.获取扩展类加载器
            //ClassLoader classLoader2 = classLoader1.getParent();
            ClassLoader classLoader2 = ClassLoader.getSystemClassLoader().getParent();
            System.out.println(classLoader2);
            //3.获取系统类加载器
            ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
            System.out.println(classLoader1);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
