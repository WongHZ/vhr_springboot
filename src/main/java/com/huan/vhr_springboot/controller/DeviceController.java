package com.huan.vhr_springboot.controller;

import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.Community;
import com.huan.vhr_springboot.entity.Device;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.service.DeviceService;
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
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class DeviceController {

    @Resource
    DeviceService deviceService;
    @Resource
    CommunityService communityService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;

    @GetMapping(value = "/device_list",produces = {"application/json;charset=UTF-8"})
    public String device_list(@RequestParam("page") Long pageNo,
                              @RequestParam("community_id") Long cid,
                              Model model){
        //查询数据并分页
        List<Device> deviceList = deviceService.selectAllDevice(pageNo,cid);
        List<Community> communities = communityService.selectAllCommunityName();
        //查询总数据量
        Integer total = deviceService.total(cid);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = deviceService.pageNum(total);
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
        model.addAttribute("device_list",deviceList);
        model.addAttribute("communities",communities);
        model.addAttribute("current",pageNo);
        model.addAttribute("community_id",cid);
        model.addAttribute("port",port.getPort());
        return "device_list.html";
    }

    @PostMapping(value = "/devicesearch",produces = {"application/json;charset=UTF-8"})
    public String parkingsearch(@RequestParam("community_id") Long cid){
        return "redirect:device_list?page=1&community_id=" + cid;
    }

    @GetMapping("/device_add")
    public String device_add(Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("port",port.getPort());
        return "device_add.html";
    }

    @PostMapping("/device_addData")
    @ResponseBody
    public Integer device_addData(@RequestParam("community_id") Long cid,@RequestParam("device_name") String name,
                                  @RequestParam("brand") String brand,@RequestParam("money") String money,
                                  @RequestParam("number") Integer number,@RequestParam("eu_life") Integer eulife,
                                  @RequestParam("building_id") String bname,@RequestParam("type") String type){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Community community = communityService.selectDataByCidOrCname(cid,null);
        StringBuilder code = new StringBuilder();
        code.append(community.getC_code().substring(0,2)).append(bname.substring(0,1))
                .append(type).append(timestamp.toString().substring(0,10).replace("-",""))
                .append(makeUtil.redisttl(700));
        BigDecimal price = new BigDecimal(money);
        Device device = new Device(0L,cid,code.toString(),type,name,brand,price,number,eulife,timestamp,timestamp);
        Integer result = deviceService.addDevice(device);
        return result;
    }

    @GetMapping("/device_update")
    public String device_update(@RequestParam("id") Long did,
                                @RequestParam("page") Integer page, Model model){
        Device device = deviceService.selectDataBydidOrDname(did,0L,null);
        String build = device.getCode().substring(2,3);
        Community community = communityService.selectDataByCidOrCname(device.getCid(),null);
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("build",build);
        model.addAttribute("com",community);
        model.addAttribute("device",device);
        model.addAttribute("current",page);
        model.addAttribute("port",port.getPort());
        return "device_update.html";
    }

    @PostMapping("/device_upData")
    @ResponseBody
    public Integer device_upData(@RequestParam("community_id") Long cid,@RequestParam("device_name") String name,
                                 @RequestParam("brand") String brand,@RequestParam("money") String money,
                                 @RequestParam("number") Integer number,@RequestParam("eu_life") Integer eulife,
                                 @RequestParam("building_id") String bname,@RequestParam("type") String type,
                                 @RequestParam("page") Integer page,@RequestParam("did") Long did){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BigDecimal price = new BigDecimal(money);
        Device device = new Device(did,cid,type,name,brand,price,number,eulife,timestamp);
        Integer result = deviceService.upDevice(device,page);
        return result;
    }

    @PostMapping("/devicedel")
    @ResponseBody
    public Integer devicedel(@RequestParam("id") Long id){
        Integer result = deviceService.delDevice(id);
        return result;
    }
}
