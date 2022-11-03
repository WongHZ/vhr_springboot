package com.huan.vhr_springboot.controller;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.service.*;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class ParkingUseController {
    @Resource
    ParkingUseService parkingUseService;
    @Resource
    ParkingService parkingService;
    @Resource
    PersonnelService personnelService;
    @Resource
    CarService carService;
    @Resource
    CommunityService communityService;
    @Resource
    MakeUtil makeUtil;

    @GetMapping(value = "/parkingusagelist",produces = {"application/json;charset=UTF-8"})
    public String parkingusagelist(@RequestParam("page") Long pageNo,
                              @RequestParam("parking_code") String parkingCode,
                              Model model){
        parkingCode = URLDecoder.decode(parkingCode, StandardCharsets.UTF_8);
        Long pid = parkingService.selectParkingByparkingcode(parkingCode);
        //查询数据并分页
        List<ParkingUse> parkingUseslist = parkingUseService.selectAllParkingUse(pageNo,pid);
        List<Community> communities = communityService.selectAllCommunityName();
        //查询总数据量
        Integer total = parkingUseService.total(pid);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = parkingUseService.pageNum(total);
        List<Integer> pagelist = makeUtil.turnlist(p);
        /**
         * 1、返回总数据量
         * 2、返回总页数
         * 3、返回页数list
         * 4、返回当前所在页数,一定不能是字符串类型
         * 5、返回houseId
         */
        model.addAttribute("total",total);
        model.addAttribute("total_page",p);
        model.addAttribute("total_count",pagelist);
        model.addAttribute("parker_list",parkingUseslist);
        model.addAttribute("communities",communities);
        model.addAttribute("current",pageNo);
        model.addAttribute("parking_code",pid);
        return "parkingusagelist.html";
    }

    @PostMapping("/parkingUseSearch")
    public String parkingUseSearch(@RequestParam("page") Integer pageNo,
                                   @RequestParam("parking_code") String parkingCode){
        return "redirect:parkingusagelist?page=" + pageNo + "&parking_code=" + parkingCode;
    }

    @GetMapping("/parkinguseadd")
    public String parkinguseadd(Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        return "parkingusageadd.html";
    }

    @PostMapping(value = "/parkinguseaddData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public Integer parkinguseaddData(@RequestParam("community_id") Long cid, @RequestParam("parking_code") String parkingCode,
                                     @RequestParam("car_code") String carCode, @RequestParam("personnel_name") String personName,
                                     @RequestParam("type") Integer type, @RequestParam("money")BigDecimal money,
                                     @RequestParam("start_time")String startTime,@RequestParam("end_time") String endTime){
        Parking parking = parkingService.selectParkingByCarcode(parkingCode);
        if(StrUtil.isBlankIfStr(parking)){
            return 0;
        }
        if(parking.getStatus() == 1){
            return 2;
        }
        Car car = carService.selectCarBycarCode(carCode);
        if(StrUtil.isBlankIfStr(car)){
            return 0;
        }
        Long checkId = parkingUseService.selectIdBycarId(car.getId());
        if(checkId != 0){
            return 3;
        }
        Personnel personnel = personnelService.selectDataByName(personName);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Date stime = makeUtil.turnDate(startTime);
        Date etime = makeUtil.turnDate(endTime);
        ParkingUse parkingUse = new ParkingUse(0L,cid,parking.getId(), car.getId(), personnel.getPid(),
                type,money,stime,etime,timestamp,timestamp);
        Integer re = parkingService.changeStatus(parking.getId(),1);
        Integer re1 = parkingUseService.addParkingUse(parkingUse);
        Integer result = 0;
        if(re == re1){
            result = 1;
        }
        return result;
    }

    @GetMapping("/parkinguseupdate")
    public String parkinguseupdate(@RequestParam("id") Long id, Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        ParkingUse parkingUse = parkingUseService.selectDataById(id);
        Community community = communityService.selectDataByCidOrCname(parkingUse.getCid(),null);
        model.addAttribute("communities",communities);
        model.addAttribute("cname",community.getCname());
        model.addAttribute("parkingUse",parkingUse);
        model.addAttribute("id",id);
        return "parkinguseupdate.html";
    }

    @PostMapping("/parkinguseupData")
    @ResponseBody
    public Integer parkinguseupData(@RequestParam("id") Long id,@RequestParam("community_id") Long cid,
                                    @RequestParam("parking_code") String parkingCode,
                                    @RequestParam("car_code") String carCode, @RequestParam("personnel_name") String personName,
                                    @RequestParam("type") Integer type, @RequestParam("money")BigDecimal money,
                                    @RequestParam("start_time")String startTime,@RequestParam("end_time") String endTime,
                                    @RequestParam("old_pcode") String oldpcode, @RequestParam("old_ccode") String old_ccode,
                                    @RequestParam("old_pname") String old_pname, @RequestParam("old_money")BigDecimal old_money,
                                    @RequestParam("old_start")String old_start,@RequestParam("old_end") String old_end){

        Parking parking;
        if(! StrUtil.equals(parkingCode,oldpcode)){
            parking = parkingService.selectParkingByCarcode(parkingCode);
            if(StrUtil.isBlankIfStr(parking)){
                return 0;
            }
            if(parking.getStatus() == 1){
                return 2;
            }
            Integer re = parkingService.changeStatus(parking.getId(),1);
            Parking oldparking = parkingService.selectParkingByCarcode(oldpcode);
            Integer re1 = parkingService.changeStatus(oldparking.getId(),0);
            if(re != re1){
                return 0;
            }
        }else {
            parking = parkingService.selectParkingByCarcode(oldpcode);
        }

        Car car;
        if(! StrUtil.equals(carCode,old_ccode)){
            car = carService.selectCarBycarCode(carCode);
            if(StrUtil.isBlankIfStr(car)){
                return 0;
            }
            Long checkId = parkingUseService.selectIdBycarId(car.getId());
            if(checkId != 0){
                return 3;
            }
        }else {
            car = carService.selectCarBycarCode(old_ccode);
        }

        Personnel personnel;
        if(! StrUtil.equals(personName,old_pname)){
            personnel = personnelService.selectDataByName(personName);
        }else {
            personnel = personnelService.selectDataByName(old_pname);
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Date stime;
        if(StrUtil.isBlank(startTime)){
           stime = makeUtil.turnDate(old_start);
        }else {
           stime = makeUtil.turnDate(startTime);
        }

        Date etime;
        if(StrUtil.isBlank(endTime)){
            etime = makeUtil.turnDate(old_start);
        }else {
            etime = makeUtil.turnDate(endTime);
        }

        if(money == old_money){
            money = old_money;
        }

        ParkingUse parkingUse = new ParkingUse(id,cid,parking.getId(), car.getId(),
                personnel.getPid(), type,money,stime,etime,timestamp);
        Integer result = parkingUseService.upParkingUse(parkingUse);
        return result;
    }

    @PostMapping("/parkingusedel")
    @ResponseBody
    public Integer parkingusedel(@RequestParam("id") Long id){
        Integer result = parkingUseService.parkingusedel(id);
        return result;
    }
}
