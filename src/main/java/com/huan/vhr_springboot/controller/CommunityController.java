package com.huan.vhr_springboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.entity.Building;
import com.huan.vhr_springboot.entity.Community;
import com.huan.vhr_springboot.entity.LoginUser;
import com.huan.vhr_springboot.service.AdminService;
import com.huan.vhr_springboot.service.BuildingService;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class CommunityController {
    @Resource
    CommunityService communityservice;
    @Resource
    AdminService adminService;
    @Resource
    BuildingService buildingService;
    @Resource
    MakeUtil makeUtil;

    /**
     * 获取小区数据
     * @param pageNo 接收当前页数
     * @param adminId 接收管理员id
     * @param model 获取前端model
     * @return 转发到html模板
     */
    @GetMapping(value = "/communitylist",produces = {"application/json;charset=UTF-8"})
    public String communitylist(@RequestParam("page") Long pageNo,
                                @RequestParam("admin_id") Long adminId,
                                Model model){
        String name = "";
        if(adminId != 0L){
            LoginUser loginUser = adminService.selectDataByUidOrName(adminId,null);
            name  = loginUser.getUname();
        }
        IPage<Community> communitylist = communityservice.selectAllCommunity(pageNo,name);
        List<LoginUser> adminlist = adminService.selectAllUser();
        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = (int) communitylist.getPages();
        List<Integer> pagelist = makeUtil.turnlist(p);
        model.addAttribute("total",communitylist.getTotal());
        model.addAttribute("total_count",pagelist);
        model.addAttribute("total_page",p);
        model.addAttribute("community_list",communitylist.getRecords());
        model.addAttribute("current",communitylist.getCurrent());
        model.addAttribute("admin_id",adminId);
        model.addAttribute("admin_list",adminlist);
        return "communitylist.html";
    }

    /**
     * 根据姓名查询管理员负责的小区
     * @param pageNo 返回当前页数
     * @param adminId 返回管理员id
     * @return 通过拼接返回communitylist处真正实现该业务
     */
    @PostMapping(value = "/communitysearch",produces = {"application/json;charset=UTF-8"})
    public String searchlist(@RequestParam("page") String pageNo,
                             @RequestParam("admin_id") Long adminId){
        return "redirect:communitylist?page=" + pageNo + "&admin_id=" + adminId;
    }

    /**
     * 提供一个管理员的列表，用于
     * 1、添加小区数据的表单。
     * 2、根据管理员姓名查询负责的小区数据
     * @param model 获取前端model
     * @return 转发到html模板
     */
    @GetMapping("/communityadd")
    public String communityadd(Model model){
        //查询管理员供给管理员列表
        List<LoginUser> adminlist = adminService.selectAllUser();
        model.addAttribute("adminlist",adminlist);
        return "communityadd.html";
    }

    /**
     * 添加小区数据页
     * @param admin 管理员姓名
     * @param name 小区名称
     * @param address 小区地址
     * @param area 小区所占面积
     * @param developers 开发商名称
     * @param company 物业公司名称
     * @param greening_rate 小区绿化率
     * @param total_buildings 总栋数
     * @param total_households 总户数
     * @param status 该小区状态
     * @param images 小区缩略图
     * @param code 小区编号
     * @return 数据添加的反馈信息
     * @throws IOException
     */
    @PostMapping(value = "/communityaddData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String communityaddData(@RequestParam("user_id") String admin, @RequestParam("community_name") String name,
                               @RequestParam("cell_address") String address, @RequestParam("area_covered") BigDecimal area,
                               @RequestParam("developers") String developers, @RequestParam("property") String company,
                               @RequestParam("afforestation_rate") Integer greening_rate, @RequestParam("tung") Integer total_buildings,
                               @RequestParam("household") Integer total_households, @RequestParam("status") String status,
                               @RequestParam("images") MultipartFile images,@RequestParam("community_code") String code) throws IOException {

        //处理图片上传
        String newName = makeUtil.turnFileName(images.getOriginalFilename());
        images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\community\\",newName));
        String image = "cimage/" + newName;

        //获取时间戳
        Timestamp date = new Timestamp(System.currentTimeMillis());

        //判断将要新增的数据名字是否已在数据库里，有则直接返回
        Community selectCom = communityservice.selectDataByCidOrCname(0L,name);
        if(name.equals(selectCom.getCname())){
            return "添加失败，已有名为 [ " + name + " ]的小区";
        }

        //添加数据
        Community community = new Community(0L, code, name, address, area, total_buildings,
                total_households, greening_rate, image, developers, company, date, date, status, admin);
        Integer result = communityservice.insertCommunity(community);

        //添加小区后默认加上5栋楼
        Community comid = communityservice.selectDataByCidOrCname(0L,name);
        String buildName[] ={"A栋","B栋","C栋","D栋","E栋"};

        //得出一栋楼有多少户
        Integer buildingHold = total_households/total_buildings;

        //遍历添加到楼栋表内
        for(int i = 0;i<=buildName.length-1;i++){
            Building building = new Building(0L,comid.getCid(),name,buildName[i],buildingHold,date,date);
            buildingService.addBuilding(building);
        }

        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 根据cid更新小区数据
     * @param id 接收该条小区的id
     * @param model 获取前端model
     * @return 转发到html模板
     */
    @GetMapping("/communityupdate")
    public String communityupdate(@RequestParam("id") Long id,Model model){
        //查询管理员供给管理员列表
        List<LoginUser> adminlist = adminService.selectAllUser();
        Community community = communityservice.selectDataByCidOrCname(id,null);
        model.addAttribute("adminlist",adminlist);
        model.addAttribute("com",community);
        model.addAttribute("cid",id);
        return "communityupdate.html";
    }

    /**
     * 根据id更新小区数据
     */
    @PostMapping(value = "/communityupData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String communityupData(@RequestParam("user_id") String admin, @RequestParam("community_name") String name,
                                   @RequestParam("cell_address") String address, @RequestParam("area_covered") BigDecimal area,
                                   @RequestParam("developers") String developers, @RequestParam("property") String company,
                                   @RequestParam("afforestation_rate") Integer greening_rate, @RequestParam("tung") Integer total_buildings,
                                   @RequestParam("household") Integer total_households, @RequestParam("status") String status,
                                   @RequestParam("images") MultipartFile images,@RequestParam("community_code") String code,
                                   @RequestParam("cid") Long id,@RequestParam("cimage") String oldImage) throws IOException {

        String image = "";
        if(!images.isEmpty()){
            //如果更新了图片，则处理图片上传
            String newName = makeUtil.turnFileName(images.getOriginalFilename());
            images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\community\\",newName));
            image = "cimage/" + newName;
        }else {
            //没有则使用原来的图片路径
            log.info("-----------图片路径在{}--------------",oldImage);
            image = oldImage;
        }

        //获取时间戳
        Timestamp date = new Timestamp(System.currentTimeMillis());

        //根据id更新小区数据，覆盖更新时间，保留创建时间
        Community community = new Community(id, code, name, address, area, total_buildings,
                total_households, greening_rate, image, developers, company, date, status, admin);
        Integer result = communityservice.updateCommunityById(community);

        Integer totalBuildingHouse = total_households/total_buildings;
        Integer resultBuilding = buildingService.updateBuildingNameByCommunityName(name,totalBuildingHouse);

        if(result == 1 && resultBuilding == 5){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 删除数据接口
     */
    @PostMapping(value = "/communitydel")
    @ResponseBody
    public void communitydel(@RequestParam("id") Long cid){
        //根据传过来的小区id查找小区名
        Community community = communityservice.selectDataByCidOrCname(cid,null);

        //根据传过来的小区名把楼栋表里有关此名的数据删除
        Integer resultBuild = buildingService.delByCommunityName(community.getCname());

        //删除小区列表里的小区数据
        Integer result = communityservice.deleteCommunityById(cid);
        log.info("----删除该条小区数据{}，删除该条楼栋数据-------",result,resultBuild);
    }

}
