package com.huan.vhr_springboot.controller;

import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.Complaint;
import com.huan.vhr_springboot.entity.Repair;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.service.ComplaintService;
import com.huan.vhr_springboot.service.PersonnelService;
import com.huan.vhr_springboot.service.RepairService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class IndexController {
    @Resource
    @Autowired
    CommunityService communityservice;
    @Resource
    @Autowired
    PersonnelService personnelService;
    @Resource
    @Autowired
    ComplaintService complaintService;
    @Resource
    @Autowired
    RepairService repairService;
    @Resource
    @Autowired
    RedisTemplate redisTemplate;
    @Resource
    @Autowired
    MakeUtil makeUtil;
    @Resource
    @Autowired
    Port port;

    @GetMapping("/welcome")
    public String welcome(Model model){
        BigDecimal totalBuildings = communityservice.countTotalBuildings();
        BigDecimal totalHouseholds = communityservice.countTotalHouseholds();
        Integer totalPersons = personnelService.total(0L);
        Integer totalTenants = personnelService.totalTenant();
        List<Repair> repairs = repairService.welData();
        Map<Integer,Repair> times;
        if(! redisTemplate.hasKey("wel_repair")){
            times = turnList(repairs);
            redisTemplate.opsForValue().set("wel_repair",times,makeUtil.redisttl(86400), TimeUnit.SECONDS);
            log.info("维修表需要转换");
        }else {
            times = (Map<Integer,Repair>) redisTemplate.opsForValue().get("wel_repair");
            log.info("维修表直接从redis获取转换");
        }
        model.addAttribute("times",times);
        model.addAttribute("repairs",repairs);
        model.addAttribute("tTenants",totalTenants);
        model.addAttribute("tPersons",totalPersons);
        model.addAttribute("tBuildings",totalBuildings);
        model.addAttribute("tHouseholds",totalHouseholds);
        model.addAttribute("port",port.getPort());
        return "welcome.html";
    }

    private Map<Integer,Repair> turnList(List<Repair> repairs){
        Map<Integer,Repair> times = new HashMap<>();
        int i = 1;
        for(Repair list : repairs){
            String a = list.getCreatetime().toString().substring(5,10);
            Repair repair = new Repair(list.getRid(),0L,a,0L,null,
                    0L,null,null,list.getDescr(),list.getStatus(),
                    null,null,null);
            times.put(i,repair);
            i++;
        }
        return times;
    }

    @GetMapping("/index")
    public String index(HttpServletRequest request,Model model){
        String head = request.getHeader("Cookie");
        String token = makeUtil.getToken(head);
        String image = makeUtil.getUserData(token,"image");
        String username = makeUtil.getUserData(token,"username");
        String rolename = makeUtil.getUserData(token,"role_name");
        Integer totalComplaint = complaintService.totalNew();
        model.addAttribute("image",image);
        model.addAttribute("uname",username);
        model.addAttribute("rname",rolename);
        model.addAttribute("tComplaints",totalComplaint);
        model.addAttribute("port",port.getPort());
        return "index.html";
    }

    @GetMapping("/logouts")
    @ResponseBody
    public Integer logout(HttpServletResponse response){
        Cookie cookie = new Cookie("Authorization","");
        cookie.setMaxAge(2);
        response.addCookie(cookie);
        return 1;
    }

    @GetMapping("/denied")
    public String fof_error(){
        return "denied.html";
    }

    @GetMapping("/mailboxlist")
    public String mailboxlist(Model model){
        return "mailboxlist.html";
    }

    @GetMapping("/admingroup")
    public String admingroup(Model model){
        return "admingroup.html";
    }

}
