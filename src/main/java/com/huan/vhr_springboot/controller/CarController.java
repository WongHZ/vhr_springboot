package com.huan.vhr_springboot.controller;

import cn.hutool.core.net.URLDecoder;
import com.huan.vhr_springboot.entity.Car;
import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.service.CarService;
import com.huan.vhr_springboot.service.HouseService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;

@Controller
@Slf4j
public class CarController {
    @Resource
    CarService carService;
    @Resource
    HouseService houseService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;


    /**
     * 多表查询家庭所属车列表
     * @param pageNo 当前页数
     * @param houseId 房产证号 if 有 then 精确查询，else 全部数据
     * @param model
     * @return vehicle_list.html
     */
    @GetMapping(value = "/vehicle_list")
    public String vehicle_list(@RequestParam("page") Long pageNo,
                               @RequestParam("house_id") Long houseId,
                               Model model){

        /**
         * 根据传来的房产号去个人房产表查询该条数据的id，
         * if houseId=0 then 说明并无此条数据或查找全部数据 return 全部成员数据
         *  else houseId!=0 then 为精确查找，return 该条数据的houseId
         */
        //查询数据并分页
        List<Car> carslist = carService.selectAllCar(pageNo,houseId);

        //查询总数据量
        Integer total = carService.total(houseId);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = carService.pageNum(total);
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
        model.addAttribute("car_list",carslist);
        model.addAttribute("current",pageNo);
        model.addAttribute("house_id",houseId);
        model.addAttribute("port", port.getPort());
        return "vehicle_list.html";
    }

    /**
     * 根据搜索房产证号查找该房产登记的相关车辆跳转
     * @param pageNo 当前页数
     * @param name 房产证号
     * @return vehicle_list
     */
    @PostMapping(value = "/carsearch",produces = {"application/json;charset=UTF-8"})
    public String carsearch(@RequestParam("page") String pageNo,
                               @RequestParam("house_name") String name){
        String houseName = URLDecoder.decode(name, StandardCharsets.UTF_8);
        Long houseId = houseService.selectHidByHname(houseName);
        return "redirect:vehicle_list?page=" + pageNo + "&house_id=" + houseId;
    }

    /**
     * 添加车辆数据的跳转接口
     * @param model
     * @return personnel_add.html
     */
    @GetMapping("/vehicle_add")
    public String vehicle_add(Model model){
        return "vehicle_add.html";
    }

    /**
     * 添加车辆的表单提交
     * @param houseCode 房产证号，绑定此车辆信息是哪家的
     * @param carNum 车牌号
     * @param color 颜色
     * @param images 车辆图片
     * @return 操作数据的信息
     * @throws IOException
     */
    @PostMapping("/vehicle_addData")
    @ResponseBody
    public String vehicle_addData(@RequestParam("houseCode") String houseCode,@RequestParam("veh_num") String carNum,
                                  @RequestParam("veh_color") String color,@RequestParam("images") MultipartFile images) throws IOException {

        //处理图片上传
        String newName = makeUtil.turnFileName(images.getOriginalFilename());
        images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\car\\",newName));
        String image = "carimage/" + newName;

        //获取时间戳
        Timestamp date = new Timestamp(System.currentTimeMillis());

        Long cId = carService.selectCaridByCarnum(carNum);
        if (cId != 0) {
            return "该车牌已有录入车辆，请重新检查输入，如有问题请联系相关人员处理！";
        }

        //根据房产证号查询该条数据的id
        Long hid = houseService.selectHidByHcode(houseCode);

        //添加新的数据
        Car car = new Car(0L,hid,carNum,color,image,date,date,0);
        Integer result = carService.addCar(car);

        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 更新车辆数据的跳转接口
     * @param model
     * @return vehicle_update.html
     */
    @GetMapping("/vehicle_update")
    public String vehicle_update(@RequestParam("id") Long id,
                                 @RequestParam("page") Integer page,Model model){
        Car car = carService.selectDataById(id);
        model.addAttribute("car",car);
        model.addAttribute("current",page);
        return "vehicle_update.html";
    }

    /**
     * 更新车辆数据的表单提交
     * @param cid 该条数据的id
     * @param houseCode 所属房产证号
     * @param carNum 车牌号
     * @param color 颜色
     * @param oldImage 旧图片地址
     * @param images 新图片地址
     * @return 操作数据信息
     * @throws IOException
     */
    @PostMapping("/vehicle_upData")
    @ResponseBody
    public String vehicle_upData(@RequestParam("id") Long cid, @RequestParam("houseCode") String houseCode,
                                 @RequestParam("veh_num") String carNum, @RequestParam("veh_color") String color,
                                 @RequestParam("oldImage") String oldImage,@RequestParam("images") MultipartFile images,
                                 @RequestParam("page") Integer page) throws IOException {

        //如有新图用新图，没有用旧图地址
        String image;
        if(! images.isEmpty()){
            //处理图片上传
            String newName = makeUtil.turnFileName(images.getOriginalFilename());
            images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\car\\",newName));
            image = "carimage/" + newName;
        }else {
            image = oldImage;
        }


        //获取时间戳，用于更新时间
        Timestamp date = new Timestamp(System.currentTimeMillis());

        //根据房产证号查询该条数据的id
        Long hid = houseService.selectHidByHcode(houseCode);

        //更新车辆信息
        Car car = new Car(cid,hid,carNum,color,image,date,0);
        Integer result = carService.updateCar(car,page);

        if(result == 1){
            return "数据更新成功，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 删除数据
     * @param cid
     */
    @PostMapping("/cardel")
    @ResponseBody
    public Integer cardel(@RequestParam("id") Long cid){
        Car car = carService.selectDataById(cid);
        Integer result = carService.cardel(car);
        log.info("----删除该条车辆数据{}-------",result);
        return result;
    }
}
