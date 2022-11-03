package com.huan.vhr_springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.service.AdminService;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.service.ComplaintService;
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
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class ComplainController {
    @Resource
    ComplaintService complaintService;
    @Resource
    CommunityService communityService;
    @Resource
    PersonnelService personnelService;
    @Resource
    AdminService adminService;
    @Resource
    MakeUtil makeUtil;

    @GetMapping(value = "/complainlist",produces = {"application/json;charset=UTF-8"})
    public String complainlist(@RequestParam("page") Long pageNo,
                               @RequestParam("admin_id") Long aid,
                               Model model){
        String adminName = "";
        if(aid != 0L){
            LoginUser loginUser = adminService.selectDataByUidOrName(aid,null);
            adminName = loginUser.getUname();
        }
        //查询数据并分页
        List<Complaint> complaintList = complaintService.selectAllComplaint(pageNo,adminName);
        List<LoginUser> adminlist = adminService.selectAllUser();
        //查询总数据量
        Integer total = complaintService.total(adminName);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = complaintService.pageNum(total);
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
        model.addAttribute("complaint_list",complaintList);
        model.addAttribute("admin_list",adminlist);
        model.addAttribute("current",pageNo);
        model.addAttribute("admin_id",aid);
        return "complainlist.html";
    }

    /**
     * 根据姓名查询管理员负责的维修登记
     * @param pageNo 返回当前页数
     * @param adminId 返回管理员id
     * @return 通过拼接返回communitylist处真正实现该业务
     */
    @PostMapping(value = "/complainsearch",produces = {"application/json;charset=UTF-8"})
    public String searchlist(@RequestParam("page") String pageNo,
                             @RequestParam("admin_id") Long adminId){
        return "redirect:complainlist?page=" + pageNo + "&admin_id=" + adminId;
    }

    @GetMapping("/complainadd")
    public String complainadd(HttpServletRequest request, Model model){
        String head = request.getHeader("Cookie");
        String token = makeUtil.getToken(head);
        String username = makeUtil.getUserData(token,"username");
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("admin_name",username);
        return "complainadd.html";
    }

    @PostMapping("/complainaddData")
    @ResponseBody
    public Integer complainaddData(@RequestParam("community_id") Long cid, @RequestParam("admin_name") String adminName,
                                 @RequestParam("username") String pname, @RequestParam("descr") String description){
        Long pid = personnelService.selectOidByOname(pname);
        Complaint complaint = new Complaint(0L,cid,pid,description,adminName);
        Integer result = complaintService.addComplaint(complaint);
        return result;
    }

    @PostMapping("/complainget_person")
    @ResponseBody
    public Integer complain_person(@RequestParam("pname") String pname){
        Personnel personnel = personnelService.selectDataByName(pname);
        if(StrUtil.isBlankIfStr(personnel)){
            return 0;
        }else {
            return 1;
        }
    }

    @GetMapping("/complainupdate")
    public String complainupdate(@RequestParam("id") Long cpid,Model model){
        Complaint complaint = complaintService.selectDataByCid(cpid);
        Community community = communityService.selectDataByCidOrCname(complaint.getCid(),null);
        Personnel personnel = personnelService.selectByPid(complaint.getPid());
        complaint.setCname(community.getCname());
        complaint.setPname(personnel.getPname());
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("complaints",complaint);
        return "complainupdate.html";
    }

    @PostMapping("/complainupData")
    @ResponseBody
    public Integer complainupData(@RequestParam("community_id") Long cid, @RequestParam("username") String pname,
                                  @RequestParam("descr") String description, @RequestParam("cpid") Long cpid){
        Long pid = personnelService.selectOidByOname(pname);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Complaint complaint = new Complaint(cpid,cid,pid,description,timestamp);
        Integer result = complaintService.upComplaint(complaint);
        return result;
    }

    /**
     * 更改该条数据状态
     * @param id 该条数据id
     * @param status 状态
     * @return 返回状态信息
     */
    @PostMapping("/complainstatus")
    @ResponseBody
    public Integer changeStatus(@RequestParam("id") Long id,
                                @RequestParam("status") Integer status){
        if(status == 1){
            status = 2;
        }else if(status == 0){
            status = 1;
        }
        Integer result = complaintService.changeStatus(id,status);
        return result;
    }

    /**
     * 删除数据接口
     */
    @PostMapping(value = "/complaindel")
    @ResponseBody
    public Integer complaindel(@RequestParam("id") Long cpid){
        Integer result = complaintService.delComplaint(cpid);
        return result;
    }
}
