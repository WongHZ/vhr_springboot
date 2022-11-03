package com.huan.vhr_springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.service.*;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class RepairController {
    @Resource
    RepairService repairService;
    @Resource
    CommunityService communityService;
    @Resource
    PersonnelService personnelService;
    @Resource
    DeviceService deviceService;
    @Resource
    AdminService adminService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    MakeUtil makeUtil;

    @GetMapping(value = "/repairlist",produces = {"application/json;charset=UTF-8"})
    public String repairlist(@RequestParam("page") Long pageNo,
                             @RequestParam("admin_id") Long aid,
                             Model model){
        String adminName = "";
        if(aid != 0L){
            LoginUser loginUser = adminService.selectDataByUidOrName(aid,null);
            adminName = loginUser.getUname();
        }
        //查询数据并分页
        List<Repair> repairList = repairService.selectAllRepair(pageNo,adminName);
        List<LoginUser> adminlist = adminService.selectAllUser();
        //查询总数据量
        Integer total = repairService.total(adminName);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = repairService.pageNum(total);
        List<Integer> pagelist = makeUtil.turnlist(p);
        /**
         * 1、返回总数据量
         * 2、返回总页数
         * 3、返回页数list
         * 4、返回当前所在页数,一定不能是字符串类型
         * 5、返回cid
         */
        model.addAttribute("total",total);
        model.addAttribute("total_page",p);
        model.addAttribute("total_count",pagelist);
        model.addAttribute("repair_list",repairList);
        model.addAttribute("admin_list",adminlist);
        model.addAttribute("current",pageNo);
        model.addAttribute("admin_id",aid);
        return "repairlist.html";
    }

    /**
     * 根据姓名查询管理员负责的维修登记
     * @param pageNo 返回当前页数
     * @param adminId 返回管理员id
     * @return 通过拼接返回communitylist处真正实现该业务
     */
    @PostMapping(value = "/repairsearch",produces = {"application/json;charset=UTF-8"})
    public String searchlist(@RequestParam("page") String pageNo,
                             @RequestParam("admin_id") Long adminId){
        return "redirect:repairlist?page=" + pageNo + "&admin_id=" + adminId;
    }

    @GetMapping("/repairadd")
    public String repairadd(HttpServletRequest request,Model model){
        String head = request.getHeader("Cookie");
        String token = makeUtil.getToken(head);
        String username = makeUtil.getUserData(token,"username");
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("admin_name",username);
        return "repairadd.html";
    }

    @PostMapping("/repairaddData")
    @ResponseBody
    public Integer repairaddData(@RequestParam("community_id") Long cid,@RequestParam("device_id") String dname,
                                 @RequestParam("admin_name") String adminName,@RequestParam("username") String pname,
                                 @RequestParam("descr") String description){
        Device device = deviceService.selectDataBydidOrDname(0L,cid,dname);
        Long pid = personnelService.selectOidByOname(pname);
        Repair repair = new Repair(0L,cid,pid,device.getDid(),adminName,description);
        Integer result = repairService.addRepair(repair);
        return result;
    }


    @PostMapping("/repairget_device")
    @ResponseBody
    public String[] repairget_device(@RequestParam("cid") Long cid){
        List<Device> devices = deviceService.selectAllDeviceName(cid);
        String[] name = new String[devices.size()];
        for(int i = 0; i < name.length;i++){
            name[i] = devices.get(i).getName();
        }
        return name;
    }

    @PostMapping("/repairget_person")
    @ResponseBody
    public Integer repairget_person(@RequestParam("pname") String pname){
        Personnel personnel = personnelService.selectDataByName(pname);
        if(StrUtil.isBlankIfStr(personnel)){
            return 0;
        }else {
            return 1;
        }
    }

    @GetMapping("/repairupdate")
    public String repairupdate(@RequestParam("id") Long rid,Model model){
        Repair repair = repairService.selectDataByridOrRname(rid,0L,null);
        Community community = communityService.selectDataByCidOrCname(repair.getCid(),null);
        Personnel personnel = personnelService.selectByPid(repair.getPid());
        repair.setCname(community.getCname());
        repair.setPname(personnel.getPname());
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("repairs",repair);
        return "repairupdate.html";
    }

    @PostMapping("/repairupData")
    @ResponseBody
    public Integer repairupData(@RequestParam("community_id") Long cid,@RequestParam("device_id") String dname,
                                @RequestParam("username") String pname, @RequestParam("descr") String description,
                                @RequestParam("rid") Long rid){
        Long pid = personnelService.selectOidByOname(pname);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Device device = deviceService.selectDataBydidOrDname(0L,cid,dname);
        Repair repair = new Repair(rid,cid,pid,device.getDid(),null,description,timestamp);
        return repairService.updateRepair(repair);
    }

    /**
     * 更改该条数据状态
     * @param id 该条数据id
     * @param status 状态
     * @return 返回状态信息
     */
    @PostMapping("/repairstatus")
    @ResponseBody
    public Integer changeStatus(@RequestParam("id") Long id,
                                @RequestParam("status") Integer status){
        if(status == 1){
            status = 2;
        }else if(status == 0){
            status = 1;
        }
        return repairService.changeStatus(id,status);
    }

    /**
     * 删除数据接口
     */
    @PostMapping(value = "/repairdel")
    @ResponseBody
    public Integer repairdel(@RequestParam("id") Long rid){
        return repairService.delRepair(rid);
    }
}
