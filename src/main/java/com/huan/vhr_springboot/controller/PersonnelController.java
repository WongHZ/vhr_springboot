package com.huan.vhr_springboot.controller;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.entity.enums.PersonTypeEnum;
import com.huan.vhr_springboot.entity.enums.SexEnum;
import com.huan.vhr_springboot.service.CommunityService;
import com.huan.vhr_springboot.service.HouseService;
import com.huan.vhr_springboot.service.PersonnelService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Controller
public class PersonnelController {
    @Resource
    PersonnelService personnelService;
    @Resource
    CommunityService communityService;
    @Resource
    HouseService houseService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    Port port;
    /**
     * 获取成员数据
     * @param pageNo 接收当前页数
     * @param code 接收当前精确查找的房产名
     * @param model 获取前端model
     * @return 转发到html模板
     */
    @GetMapping(value = "/personnel_list",produces = {"application/json;charset=UTF-8"})
    public String personnel_list(@RequestParam("page") Long pageNo,
                                @RequestParam("house_code") String code,
                                Model model){
        String houseCode = URLDecoder.decode(code,StandardCharsets.UTF_8);
        /**
         * 根据传来的房产号去个人房产表查询该条数据的id，
         * if houseId=0 then 说明并无此条数据或查找全部数据 return 全部成员数据
         *  else houseId!=0 then 为精确查找，return 该条数据的houseId
         */
        Long houseId = houseService.selectHidByHcode(houseCode);

        //查询数据并分页
        List<Personnel> personnellist = personnelService.selectAllPersonnel(pageNo,houseId);

        //查询总数据量
        Integer total = personnelService.total(houseId);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = personnelService.pageNum(total);
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
        model.addAttribute("personnel_list",personnellist);
        model.addAttribute("current",pageNo);
        model.addAttribute("house_code",houseCode);
        model.addAttribute("port",port.getPort());
        return "personnel_list.html";
    }

    /**
     * 根据搜索房产证号查找该房产登记的相关成员跳转
     * @param pageNo 当前页数
     * @param code 房产证号
     * @return html模板，跳转回personnel_list
     */
    @PostMapping("/personsearch")
    public String personsearch(@RequestParam("page") String pageNo,
                              @RequestParam("house_code") String code){
        String houseCode = URLDecoder.decode(code,StandardCharsets.UTF_8);
        return "redirect:personnel_list?page=" + pageNo + "&house_code=" + houseCode;
    }

    /**
     * 添加相关成员数据的跳转接口
     * @param model
     * @return personnel_add.html
     */
    @GetMapping("/personnel_add")
    public String personnel_add(Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        model.addAttribute("communities",communities);
        return "personnel_add.html";
    }

    /**
     * 提交成员数据的接口
     * @param cid 小区号
     * @param hName 房产名
     * @param pName 成员姓名
     * @param identity 身份证号
     * @param birthday 出生日期
     * @param phone 手机号
     * @param sex 性别
     * @param type 成员类型：1、业主，2、家庭成员，3、租客
     * @param images 成员图片
     * @return 操作数据信息
     * @throws IOException
     */
    @PostMapping(value = "/personneladdData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String personneladdData(@RequestParam("community_id") Long cid, @RequestParam("house_name") String hName,
                                   @RequestParam("member_name") String pName, @RequestParam("identity") String identity,
                                   @RequestParam("birthday") String birthday, @RequestParam("phone") Long phone,
                                   @RequestParam("sex") Integer sex, @RequestParam("personnel_type") Integer type,
                                   @RequestParam("images") MultipartFile images) throws IOException {

        //处理图片上传
        if(images.isEmpty()){
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
        String newName = makeUtil.turnFileName(images.getOriginalFilename());
        images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\personnel\\",newName));
        String image = "pimage/" + newName;

        //获取时间戳
        Timestamp date = new Timestamp(System.currentTimeMillis());

        //生日转换为sqldate类型
        Date realDate = makeUtil.turnDate(birthday);

        //判断将要新增的数据名字是否已在数据库里，有则直接返回
        Personnel personCheck = personnelService.selectDataByName(pName);
        String selectName = personCheck.getPname();
        if(pName.equals(selectName)){
            return "添加失败，已有名为 [ " + pName + " ]的成员";
        }

        Long hid = houseService.selectHidByHname(hName);
        if(hid == 0L){
            return "暂无此房产证明，请确认信息是否正确或咨询管理员！";
        }

        //添加数据,调用接口内方法添加成员信息
        Personnel personnel = addPerson(0L,cid,hid,pName,image,identity,phone,sex,realDate,type,date,date);
        Integer result = personnelService.personneladd(personnel);
        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 更新成员信息的接口跳转
     * @param pid 该条数据的id
     * @param model
     * @return
     */
    @GetMapping("/personnel_update")
    public String personnel_update(@RequestParam("id") Long pid, Model model){
        List<Community> communities = communityService.selectAllCommunityName();
        Personnel personnel = personnelService.selectByPid(pid);
        //返回表单需要的小区名
        model.addAttribute("communities",communities);
        //返回该id原本的数据
        model.addAttribute("personnel",personnel);
        return "personnel_update.html";
    }

    /**
     * 更新该数据的接口
     * @param cid 小区id
     * @param hName 房产名
     * @param pName 成员名称
     * @param identity 身份证
     * @param birthday 出生日期
     * @param phone 手机号
     * @param sex 性别
     * @param type 成员类型：1、业主，2、家庭成员，3、租客
     * @param oldImage 旧的图片路径
     * @param oldBirth 旧的出生日期
     * @param images 新的图片路径。if 有 then 用新的，弃旧的 else 用旧的
     * @param pid 该条数据的id
     * @return 操作数据的信息
     * @throws IOException
     */
    @PostMapping(value = "/personnelupData",produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String personnelupData(@RequestParam("community_id") Long cid, @RequestParam("house_name") String hName,
                                   @RequestParam("member_name") String pName, @RequestParam("identity") String identity,
                                   @RequestParam("birthday") String birthday, @RequestParam("phone") Long phone,
                                   @RequestParam("sex") Integer sex, @RequestParam("personnel_type") Integer type,
                                   @RequestParam("oimage") String oldImage, @RequestParam("obirthday") String oldBirth,
                                   @RequestParam("images") MultipartFile images,@RequestParam("pid") Long pid) throws IOException {

        String image;
        if(!images.isEmpty()){
            //如果更新了图片，则处理图片上传
            String newName = makeUtil.turnFileName(images.getOriginalFilename());
            images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\personnel\\",newName));
            image = "pimage/" + newName;
        }else {
            //没有则使用原来的图片路径
            log.info("-----------图片路径在{}--------------",oldImage);
            image = oldImage;
        }

        //获取时间戳
        Timestamp date = new Timestamp(System.currentTimeMillis());

        //生日转为sqldate类型
        if(StrUtil.isBlank(birthday)){
            birthday = oldBirth;
        }
        Date realDate = makeUtil.turnDate(birthday);

        //如输入错误的房产名则直接返回操作失败的信息
        Long hid = houseService.selectHidByHname(hName);
        if(hid == 0L){
             return "暂无此房产证明，请确认信息是否正确或咨询管理员！";
        }

        //添加数据
        Personnel personnel = addPerson(pid,cid,hid,pName,image,identity,phone,sex,realDate,type,null,date);
        Integer result = personnelService.personnelupdate(personnel);
        if(result == 1){
            return "数据成功添加，请返回系统查看！";
        }else {
            return "数据添加失败，请检查表单是否正确，如多次无法提交请联系相关人员处理！";
        }
    }

    /**
     * 删除数据
     * @param pid
     */
    @PostMapping("/personneldel")
    @ResponseBody
    public void personneldel(@RequestParam("id") Long pid){
        Integer result = personnelService.personneldel(pid);
        log.info("----删除该条成员数据{}-------",result);
    }


    /**
     * 赋值给对象
     * @param pid 成员id
     * @param cid 小区id
     * @param hid 房产id
     * @param pname 成员姓名
     * @param image 图片路径
     * @param identity 身份证号
     * @param phone 手机号
     * @param sex 性别，需判断
     * @param birth 出生日期
     * @param type 成员类型，需判断
     * @param ctime 创建时间
     * @param utime 更新时间
     * @return personnel对象
     */
    private Personnel addPerson(Long pid, Long cid, Long hid, String pname, String image,
                               String identity, Long phone, Integer sex, Date birth,
                               Integer type, @Nullable Timestamp ctime, Timestamp utime){
        Personnel personnel = new Personnel();
        personnel.setPid(pid);
        personnel.setCid(cid);
        personnel.setHid(hid);
        personnel.setPname(pname);
        personnel.setImage(image);
        personnel.setIdCard(identity);
        personnel.setPhone(phone);
        personnel.setBirthday(birth);
        if(ctime != null) {
            personnel.setCreatetime(ctime);
        }
        personnel.setUpdatetime(utime);
        if(sex == 0){
            personnel.setSex(SexEnum.MALE);
        }else {
            personnel.setSex(SexEnum.FEMALE);
        }
        if(type == 0){
            personnel.setType(PersonTypeEnum.RESIDENT);
        }else if(type == 1){
            personnel.setType(PersonTypeEnum.FAMILY);
        }else {
            personnel.setType(PersonTypeEnum.TENANT);
        }
        return personnel;
    }
}
