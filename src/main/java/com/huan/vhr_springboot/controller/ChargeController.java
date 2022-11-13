package com.huan.vhr_springboot.controller;

import cn.hutool.core.net.URLDecoder;
import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.service.ChargeService;
import com.huan.vhr_springboot.service.CommunityService;
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
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class ChargeController {

    @Resource
    ChargeService chargeService;
    @Resource
    CommunityService communityService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;

    @GetMapping(value = "/pro_list",produces = {"application/json;charset=UTF-8"})
    public String pro_list(@RequestParam("page") Long pageNo,
                             @RequestParam("chid") Long chid,
                             Model model){
        //查询数据并分页
        List<Charge> chargeList = chargeService.selectAllCharge(pageNo,chid);
        //查询总数据量
        Integer total = chargeService.total(chid);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = chargeService.pageNum(total);
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
        model.addAttribute("charge_list",chargeList);
        model.addAttribute("current",pageNo);
        model.addAttribute("chid",chid);
        model.addAttribute("port", port.getPort());
        return "pro_list.html";
    }


    /**
     * 根据姓名查询管理员负责的维修登记
     * @param pageNo 返回当前页数
     * @param code 返回管理员id
     * @return 通过拼接返回communitylist处真正实现该业务
     */
    @PostMapping(value = "/pro_search",produces = {"application/json;charset=UTF-8"})
    public String searchlist(@RequestParam("page") String pageNo,
                             @RequestParam("code") String code){
        code = URLDecoder.decode(code, StandardCharsets.UTF_8);
        Charge charge = chargeService.selectDataByChidOrCode(0L,code,null);
        return "redirect:pro_list?page=" + pageNo + "&chid=" + charge.getChid();
    }

    @GetMapping("/pro_add")
    public String pro_add(Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("port", port.getPort());
        return "pro_add.html";
    }

    @PostMapping("/pro_addData")
    @ResponseBody
    public Integer pro_addData(@RequestParam("community_id") Long cid, @RequestParam("name") String name,
                               @RequestParam("code") String code, @RequestParam("price")BigDecimal price,
                               @RequestParam("port") String port){
        Charge charge = new Charge(0L,cid,code,name,price);
        Integer result = chargeService.addCharge(charge);
        return result;
    }

    @GetMapping("/pro_update")
    public String pro_update(@RequestParam("id") Long chid,Model model){
        Charge charge = chargeService.selectDataByChidOrCode(chid,null,null);
        Community community = communityService.selectDataByCidOrCname(charge.getCid(),null);
        charge.setCname(community.getCname());
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("charges",charge);
        model.addAttribute("port", port.getPort());
        return "pro_update.html";
    }

    @PostMapping("/pro_upData")
    @ResponseBody
    public Integer pro_upData(@RequestParam("chid") Long chid,@RequestParam("community_id") Long cid,
                              @RequestParam("name") String name,@RequestParam("code") String code,
                              @RequestParam("price")BigDecimal price,@RequestParam("port") String port){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Charge charge = new Charge(chid,cid,code,name,price,timestamp);
        Integer result = chargeService.upCharge(charge);
        return result;
    }

    @PostMapping("/pro_del")
    @ResponseBody
    public Integer pro_del(@RequestParam("id") Long chid){
        Integer result = chargeService.delCharge(chid);
        return result;
    }
}
