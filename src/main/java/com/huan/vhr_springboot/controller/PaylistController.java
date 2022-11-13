package com.huan.vhr_springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.huan.vhr_springboot.config.Port;
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
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class PaylistController {
    @Resource
    CommunityService communityService;
    @Resource
    PersonnelService personnelService;
    @Resource
    ChargeService chargeService;
    @Resource
    PaylistService paylistService;
    @Resource
    AdminService adminService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;

    @GetMapping(value = "/paylist",produces = {"application/json;charset=UTF-8"})
    public String paylist(@RequestParam("page") Long pageNo,
                             @RequestParam("admin_id") Long aid,
                             Model model){
        String adminName = "";
        if(aid != 0L){
            LoginUser loginUser = adminService.selectDataByUidOrName(aid,null);
            adminName = loginUser.getUname();
        }
        //查询数据并分页
        List<Paylist> paylistList = paylistService.selectAllPaylist(pageNo,adminName);
        List<LoginUser> adminlist = adminService.selectAllUser();
        //查询总数据量
        Integer total = paylistService.total(adminName);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = paylistService.pageNum(total);
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
        model.addAttribute("paylist_list",paylistList);
        model.addAttribute("admin_list",adminlist);
        model.addAttribute("current",pageNo);
        model.addAttribute("admin_id",aid);
        model.addAttribute("port",port.getPort());
        return "paylist.html";
    }

    @GetMapping("/paylistadd")
    public String paylistadd(HttpServletRequest request, Model model){
        String head = request.getHeader("Cookie");
        String token = makeUtil.getToken(head);
        String username = makeUtil.getUserData(token,"username");
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("admin_name",username);
        model.addAttribute("port",port.getPort());
        return "paylistadd.html";
    }

    @PostMapping("/paylistaddData")
    @ResponseBody
    public Integer paylistaddData(@RequestParam("community_id") Long cid,@RequestParam("charge_id") String cname,
                                 @RequestParam("admin_name") String adminName,@RequestParam("username") String pname,
                                 @RequestParam("descr") String description,@RequestParam("pay") BigDecimal pay,
                                 @RequestParam("port") String port){
        Charge charge = chargeService.selectDataByChidOrCode(0L,null,cname);
        Long pid = personnelService.selectOidByOname(pname);
        Paylist paylist = new Paylist(0L,cid,charge.getChid(),pid,pay,description,adminName);
        Integer result = paylistService.addPaylist(paylist);
        return result;
    }

    @PostMapping("/paylist_charge")
    @ResponseBody
    public String[] paylist_charge(@RequestParam("cid") Long cid){
        List<Charge> charges = chargeService.selectDataByCid(cid);
        String[] name = new String[charges.size()];
        for(int i = 0; i < name.length;i++){
            name[i] =  charges.get(i).getName();
        }
        return name;
    }

    @PostMapping("/paylist_person")
    @ResponseBody
    public Integer paylist_person(@RequestParam("pname") String pname){
        Personnel personnel = personnelService.selectDataByName(pname);
        if(StrUtil.isBlankIfStr(personnel)){
            return 0;
        }else {
            return 1;
        }
    }

    @PostMapping("/paylist_price")
    @ResponseBody
    public String paylist_price(@RequestParam("chname") String chname){
        Charge charge = chargeService.selectDataByChidOrCode(0L,null,chname);
        return charge.getPrice().toString();
    }

    @GetMapping("/paylistupdate")
    public String paylistupdate(@RequestParam("id") Long paid,Model model){
        Paylist paylist = paylistService.selectDataByPaid(paid);
        Community community = communityService.selectDataByCidOrCname(paylist.getCid(),null);
        Personnel personnel = personnelService.selectByPid(paylist.getPid());
        Charge charge = chargeService.selectDataByChidOrCode(paylist.getChid(),null,null);
        paylist.setCname(community.getCname());
        paylist.setPname(personnel.getPname());
        paylist.setChname(charge.getName());
        paylist.setPrice(charge.getPrice());
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("paylist",paylist);
        model.addAttribute("port",port.getPort());
        return "paylistupdate.html";
    }

    @PostMapping("/paylistupData")
    @ResponseBody
    public Integer paylistupData(@RequestParam("community_id") String cname,@RequestParam("charge_id") String chname,
                                 @RequestParam("username") String pname, @RequestParam("descr") String description,
                                 @RequestParam("pay") BigDecimal pay,@RequestParam("paid") Long paid){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Paylist paylist = new Paylist(paid,pay,description,timestamp);
        Integer result = paylistService.upPaylist(paylist);
        return result;
    }

    @PostMapping("paylistdel")
    @ResponseBody
    public Integer paylistdel(@RequestParam("id") Long paid){
        Integer result = paylistService.delPaylist(paid);
        return result;
    }
}
