package com.huan.vhr_springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.entity.Community;
import com.huan.vhr_springboot.entity.House;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.service.HouseService;
import com.huan.vhr_springboot.service.PersonnelService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Controller
public class HouseController {

    @Resource
    HouseService houseService;
    @Resource
    CommunityService communityService;
    @Resource
    PersonnelService personnelService;
    @Resource
    MakeUtil makeUtil;

    /**
     * 查找个人房产所有的数据并返回到模板里
     * @param pageNo 返回当前页数
     * @param ownerId 返回业主的id
     * @param model
     * @return 返回houselist.html模板
     */
    @GetMapping(value = "/houselist",produces = {"application/json;charset=UTF-8"})
    public String houselist(@RequestParam("page") Long pageNo,
                            @RequestParam("owner_id") Long ownerId,
                            Model model){

        //根据业主id返回业主姓名
        String name = personnelService.selectOwnerNameById(ownerId);

        //返回所有房产数据
        IPage<House> houselist = houseService.selectAllHouse(pageNo,name);

        //返回一个页数list给前端页数选择使用
        int p = (int) houselist.getPages();
        List<Integer> pagelist = makeUtil.turnlist(p);

        model.addAttribute("total",houselist.getTotal());
        model.addAttribute("total_count",pagelist);
        model.addAttribute("total_page",p);
        model.addAttribute("house",houselist.getRecords());
        model.addAttribute("current",houselist.getCurrent());
        model.addAttribute("owner_id",ownerId);
        return "houselist.html";
    }

    /**
     * 增加个人房产数据的跳转
     * @param model
     * @return 返回houseadd.html的模板
     */
    @GetMapping(value = "/houseadd",produces = {"application/json;charset=UTF-8"})
    public String houseadd(Model model){

        List<Community> communitieslist = communityService.selectAllCommunityName();

        model.addAttribute("communities",communitieslist);
        return "houseadd.html";
    }

    /**
     * 上面模板接口填写完数据后提交到这里
     * @param cName 所属小区名
     * @param bName 所属楼号
     * @param floor 所属楼层
     * @param unit 所属单元
     * @param roomNum 房间数
     * @param hCode 房产证号
     * @param hName 房产名
     * @param householder 业主姓名
     * @param phone 业主手机号
     * @param time 入住时间String类型，需转换
     * @return 数据添加信息
     * @throws ParseException
     */
    @PostMapping(value = "/houseaddData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String houseaddData(@RequestParam("community_name") String cName,@RequestParam("building_name") String bName,
                               @RequestParam("floor") Integer floor,@RequestParam("unit") Integer unit,
                               @RequestParam("roomnum") Integer roomNum,@RequestParam("house_code") String hCode,
                               @RequestParam("house_name") String hName,@RequestParam("householder") String householder,
                               @RequestParam("phone") Long phone,@RequestParam("livetime") String time) throws ParseException {

        //把String类型时间字符串转化为date类型
        Date livetime = makeUtil.turnDate(time);

        //根据小区名找小区id
        Community community = communityService.selectDataByCidOrCname(0L,cName);

        //根据业主姓名找业主id
        Long oId = personnelService.selectOidByOname(householder);

        //新增数据
        House house  = new House(0L,community.getCid(),cName,bName,hCode,hName,oId,householder,phone,roomNum,unit,floor,livetime);

        Integer result = houseService.addHouse(house);
        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 更新个人房产数据的跳转
     * @param id 接收该条小区的id
     * @param model 获取前端model
     * @return 转发到html模板
     */
    @GetMapping("/houseupdate")
    public String houseupdate(@RequestParam("id") Long id,Model model){
        List<Community> communitieslist = communityService.selectAllCommunityName();
        House updateData = houseService.selectHouseByHid(id);
        model.addAttribute("communities",communitieslist);
        model.addAttribute("update_data",updateData);
        model.addAttribute("oid",id);
        return "houseupdate.html";
    }

    /**
     * 根据id查找个人房产数据并对其更新
     * @param cName 所属小区名
     * @param bName 所属楼号
     * @param floor 所属楼层
     * @param unit 所属单元
     * @param roomNum 房间数
     * @param hCode 房产证号
     * @param hName 房产名
     * @param householder 业主姓名
     * @param phone 业主手机号
     * @param time 获取入住时间string
     * @param oldtime 获取旧的入住时间string
     * @param hid 个人房产id
     * @return 返回更新信息
     * @throws ParseException
     */
    @PostMapping(value = "/houseupData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String houseupData(@RequestParam("community_name") String cName,@RequestParam("building_name") String bName,
                                  @RequestParam("floor") Integer floor,@RequestParam("unit") Integer unit,
                                  @RequestParam("roomnum") Integer roomNum,@RequestParam("house_code") String hCode,
                                  @RequestParam("house_name") String hName,@RequestParam("householder") String householder,
                                  @RequestParam("phone") Long phone,@RequestParam("livetime") String time,
                                  @RequestParam("oldtime") String oldtime,@RequestParam("hid") Long hid) throws ParseException {

        //判断时候有新的入住时间，如没有，则用旧的时间
        Date livetime;
        if(StrUtil.isNotBlank(time)){
            livetime = makeUtil.turnDate(time);
        }else {
            livetime = makeUtil.turnDate(oldtime);
        }

        //根据小区名获取小区id
        Community community = communityService.selectDataByCidOrCname(0L,cName);

        //根据业主姓名获取业主id
        Long oId = personnelService.selectOidByOname(householder);

        //执行更新数据
        House house  = new House(hid,community.getCid(),cName,bName,hCode,hName,oId,householder,phone,roomNum,unit,floor,livetime);
        Integer result = houseService.updateHouse(house);
        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 根据搜索业主姓名查找房产的跳转
     * @param pageNo 当前页数
     * @param ownername 业主姓名
     * @return html模板，跳转回houselist
     */
    @PostMapping("/housesearch")
    public String housesearch(@RequestParam("page") String pageNo,
            @RequestParam("ownername") String ownername){
        if(StrUtil.isBlank(ownername)){
            return "redirect:houselist?page=" + pageNo + "&owner_id=0";
        }
        Long oid = personnelService.selectOidByOname(ownername);
        return "redirect:houselist?page=" + pageNo + "&owner_id=" +oid;
    }

    /**
     * 根据id删除该条数据
     * @param id
     */
    @PostMapping("/housedel")
    @ResponseBody
    public Integer housedel(@RequestParam("id") Long id){
        Integer result = houseService.deleteHouseById(id);
        log.info("----删除数据代码{}，id为：{}-------",result,id);
        return result;
    }
}
