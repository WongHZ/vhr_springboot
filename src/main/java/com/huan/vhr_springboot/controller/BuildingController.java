package com.huan.vhr_springboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.Building;
import com.huan.vhr_springboot.entity.Community;
import com.huan.vhr_springboot.service.BuildingService;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
@Slf4j
@Controller
public class BuildingController {
    @Resource
    BuildingService buildingService;
    @Resource
    CommunityService communityService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;

    /**
     * 获取楼栋列表所有数据
     * @param pageNo 接收当前页数
     * @param communityId 接收小区号，当有小区号时执行精确查找
     * @param model 获取前端model
     * @return 转发到html模板
     */
    @GetMapping(value = "/buildinglist",produces = {"application/json;charset=UTF-8"})
    public String buildinglist(@RequestParam("page") Long pageNo,
                               @RequestParam("community_id") Long communityId,
                                Model model){

        //根据小区id获取小区名，再根据小区名查找所有改名旗下的楼栋数据
        String name = "";
        if(communityId != 0){
            Community community = communityService.selectDataByCidOrCname(communityId,null);
            name = community.getCname();
        }
        IPage<Building> buildinglist = buildingService.selectAllBuilding(pageNo,name);
        List<Community> communitylist = communityService.selectAllCommunityName();

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = (int) buildinglist.getPages();
        List<Integer> pagelist = makeUtil.turnlist(p);
        model.addAttribute("total",buildinglist.getTotal());
        model.addAttribute("total_count",pagelist);
        model.addAttribute("total_page",p);
        model.addAttribute("building_list",buildinglist.getRecords());
        model.addAttribute("current",buildinglist.getCurrent());
        model.addAttribute("community_id",communityId);
        model.addAttribute("community_list",communitylist);
        model.addAttribute("port", port.getPort());
        return "buildinglist.html";
    }

    /**
     * 根据小区名查询所有旗下的楼栋数据
     * @param pageNo 返回当前页数
     * @param communityId 返回小区号
     * @return 通过拼接返回buildinglist处真正实现该业务
     */
    @PostMapping(value = "/buildingsearch",produces = {"application/json;charset=UTF-8"})
    public String buildingsearch(@RequestParam("page") String pageNo,
                             @RequestParam("community_id") Long communityId){
        return "redirect:buildinglist?page=" + pageNo + "&community_id=" + communityId;
    }

    /**
     * 数据添加页面
     * @param model
     * @return
     */
    @GetMapping("/buildingadd")
    public String buildingadd(Model model){
        List<Community> communitieslist = communityService.selectAllCommunityName();
        model.addAttribute("communities",communitieslist);
        return "buildingadd.html";
    }

    /**
     * 添加楼栋数据
     * @param communityName 所属小区名
     * @param buildingName 楼栋名
     * @param totalHouse 总楼数
     * @param model
     * @return
     */
    @PostMapping(value = "/buildingaddData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String buildingaddData(@RequestParam("community_name") String communityName,
            @RequestParam("buildings_name") String buildingName, @RequestParam("households") Integer totalHouse,
                                  Model model){

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Community community = communityService.selectDataByCidOrCname(0L,communityName);
        Building building = new Building(0L,community.getCid(),communityName,buildingName,totalHouse,timestamp,timestamp);
        Integer result = buildingService.addBuilding(building);
        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 删除数据接口
     */
    @PostMapping(value = "/buildingdel")
    @ResponseBody
    public void buildingdel(@RequestParam("id") Long buildingId){
        //删除小区列表里的小区数据
        Integer result = buildingService.delBuilding(buildingId);
        log.info("----删除该条楼栋数据{}-------",result,result);
    }
}
