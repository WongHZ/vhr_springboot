package com.huan.vhr_springboot.controller;

import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.Community;
import com.huan.vhr_springboot.entity.Parking;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.service.ParkingService;
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
public class ParkingController {
    @Resource
    ParkingService parkingService;
    @Resource
    CommunityService communityService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;

    @GetMapping("/parkinglist")
    public String parkinglist(@RequestParam("page") Long pageNo,
                              @RequestParam("community_id") Long cid,
                              Model model){
        //查询数据并分页
        List<Parking> parkingslist = parkingService.selectAllParking(pageNo,cid);
        List<Community> communities = communityService.selectAllCommunityName();
        //查询总数据量
        Integer total = parkingService.total(cid);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = parkingService.pageNum(total);
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
        model.addAttribute("parking_list",parkingslist);
        model.addAttribute("communities",communities);
        model.addAttribute("current",pageNo);
        model.addAttribute("community_id",cid);
        model.addAttribute("port",port.getPort());
        return "parkinglist.html";
    }

    @PostMapping(value = "/parkingsearch",produces = {"application/json;charset=UTF-8"})
    public String parkingsearch(@RequestParam("community_id") Long cid){
        return "redirect:parkinglist?page=1&community_id=" + cid;
    }

    /**
     * 更改该条数据状态
     * @param id 该条数据id
     * @param status 状态
     * @return 返回状态信息
     */
    @PostMapping("/parkingstatus")
    @ResponseBody
    public Integer changeStatus(@RequestParam("id") Long id,
                             @RequestParam("status") Integer status){
        if(status == 1){
            status = 0;
        }else if(status == 0){
            status = 1;
        }

        Integer result = parkingService.changeStatus(id,status);
        return result;
    }

    @GetMapping("/parkingadd")
    public String parkingadd(Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        model.addAttribute("port",port.getPort());
        return "park_add.html";
    }

    @PostMapping("/parkingaddData")
    @ResponseBody
    public Integer parkingaddData(@RequestParam("community") String cName,@RequestParam("building") String bName,
                                  @RequestParam("park_num") Integer num){
        Community community = communityService.selectDataByCidOrCname(0L,cName);
        String preCode = community.getC_code().substring(0,2);

        Long cid = community.getCid();
        System.out.println("+++++++++++djighfjkwef+++++++++" + cid);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Integer result = 0;
        for(int i = 1;i <= num;i++){
            if(i <= 10){
                String code = preCode + bName + "00" + i;
                String name = cName + bName + "栋00" + i + "位";
                Parking parking = new Parking(0L,cid,code,name,0,timestamp,timestamp);
                result = parkingService.parkingadd(parking);
            }else if(i > 10 && i <= 100){
                String code = preCode + bName + "0" + i;
                String name = cName + bName + "栋0" + i + "位";
                Parking parking = new Parking(0L,cid,code,name,0,timestamp,timestamp);
                result = parkingService.parkingadd(parking);
            }else if(i > 100){
                String code = preCode + bName + i;
                String name = cName + bName + "栋" + i + "位";
                Parking parking = new Parking(0L,cid,code,name,0,timestamp,timestamp);
                result = parkingService.parkingadd(parking);
            }
        }
        return result;
    }

    @PostMapping("/parkingdel")
    @ResponseBody
    public Integer parkingdel(@RequestParam("id") Long id){
        return parkingService.parkingdel(id);
    }
}
