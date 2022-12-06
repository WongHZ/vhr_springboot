package com.huan.vhr_springboot.controller;


import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.huan.vhr_springboot.config.Port;
import com.huan.vhr_springboot.entity.*;
import com.huan.vhr_springboot.repository.CustomUserDetails;
import com.huan.vhr_springboot.service.AdminService;
import com.huan.vhr_springboot.service.ComplaintService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.Resource;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class LoginController {
    private static final Integer WEEKTTL = 604800;

    @Resource
    AuthenticationManager authenticationManager;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    RedisTemplate<String,Object> redisTemplate;
    @Resource
    AdminService adminService;
    @Resource
    MakeUtil makeUtil;
    @Resource
    ComplaintService complaintService;
    @Resource
    Port port;


    @GetMapping("/loginpage")
    public String LoginPage(){
        return "login.html";
    }

    /**
     * 登录页面
     * @param username 用户名
     * @param password 密码
     * @param response 原生响应体
     * @param model
     * @return index.html
     * @throws Exception
     */
    @PostMapping("/login")
    public String Login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpServletResponse response, Model model) throws Exception {
        //使用此对象给传来的用户名和密码验证时候正确。
        UsernamePasswordAuthenticationToken uptoken = UsernamePasswordAuthenticationToken
                .unauthenticated(username, password);
        //给验证了用户但还没给权限的对象放到authentication对象中
        Authentication authenticate = authenticationManager.authenticate(uptoken);
        //通过getPrincipal获取该账户的状态如为0则账号为停用状态
        CustomUserDetails principal = (CustomUserDetails) authenticate.getPrincipal();
        if(principal.getStatus() == 0){
            return "redirect:accountDisabled";
        }
        //把用户的相关信息状态放到map中
        String roleName = adminService.selectRole(principal.getUsername());
        Map<String,Object> payload = new HashMap<>();
        payload.put("uid",principal.getUid().toString());
        payload.put("username",principal.getUsername());
        payload.put("role_name",roleName);
        payload.put("phone",principal.getPhone().toString());
        payload.put("email",principal.getEmail());
        payload.put("image",principal.getImage());
        payload.put("login_time", LocalTime.now());
        //通过jwt工具create一个token用于权限访问时的认证
        JWTSigner jwtSigner = JWTSignerUtil.hs512("vhr_login".getBytes(StandardCharsets.UTF_8));
        String token = JWTUtil.createToken(payload, jwtSigner);
        //把用户信息放进redis里，存放时间为一个星期
        redisTemplate.opsForHash().put("login_user",principal.getUsername(),principal);
        redisTemplate.expire("login_user",makeUtil.redisttl(WEEKTTL), TimeUnit.SECONDS);
        //把token存放到cookie里，存放时间一个星期
        Cookie cookie = new Cookie("Authorization","Bearer_" + token);
        cookie.setMaxAge(604800);
        response.addCookie(cookie);
        model.addAttribute("image",principal.getImage());
        model.addAttribute("uname",principal.getUsername());
        model.addAttribute("rname",roleName);
        model.addAttribute("tComplaints",complaintService.totalNew());
        model.addAttribute("port",port.getPort());
        return "redirect:/index";
    }



    /**
     * 账号停用返回的信息
     * @return
     */
    @GetMapping("/accountDisabled")
    @ResponseBody
    public String accountDisabled(){
        return "你的账号已被停用，请联系相关人员处理！";
    }

    /**
     * 用于账户管理列表
     * @param pageNo 当前页面
     * @param rid 当前账号id
     * @param model
     * @return
     */
    @GetMapping(value = "/adminlist",produces = {"application/json;charset=UTF-8"})
    public String adminlist(@RequestParam("page") Long pageNo,
                            @RequestParam("rid") Long rid,
                                Model model){
        //查询数据并分页
        List<LoginUser> loginUsersList = adminService.selectAllLoginUser(pageNo,rid);
        List<LoginRole> roleList = adminService.roleList();
        //查询总数据量
        Integer total = adminService.total(rid);

        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = adminService.pageNum(total);
        List<Integer> pagelist = makeUtil.turnlist(p);
        /**
         * 1、返回总数据量
         * 2、返回总页数
         * 3、返回页数list
         * 4、返回当前所在页数,一定不能是字符串类型
         * 5、返回rid
         */
        model.addAttribute("total",total);
        model.addAttribute("total_page",p);
        model.addAttribute("total_count",pagelist);
        model.addAttribute("login_users",loginUsersList);
        model.addAttribute("role_names",roleList);
        model.addAttribute("current",pageNo);
        model.addAttribute("rid",rid);
        model.addAttribute("port",port.getPort());
        return "adminlist.html";
    }

    /**
     * 更改该条数据状态
     * @param id 该条数据id
     * @param status 状态
     * @return 返回状态信息
     */
    @PostMapping("/adminstatus")
    @ResponseBody
    public Integer changeStatus(@RequestParam("id") Long id,
                                @RequestParam("status") Integer status,
                                @RequestParam("page") Integer page){
        //如该条数据状态为1，则表示需要停用该账号，否则为启动该账号
        if(status == 1){
            status = 0;
        }else if(status == 0){
            status = 1;
        }
        Integer result = adminService.changeStatus(id,status,page);
        return result;
    }

    /**
     * 精确查询，传参给adminlist
     */
    @PostMapping("/adminsearch")
    public String adminsearch(@RequestParam("page_now") Long pageNo,
                              @RequestParam("rid") String rid){
        return "redirect:adminlist?page=" + pageNo + "&rid=" + rid;
    }

    /**
     * 添加管理员账号
     * @param model
     * @return
     */
    @GetMapping("/adminadd")
    public String adminadd(Model model){
        List<LoginRole> roleList = adminService.roleList();
        model.addAttribute("roleList",roleList);
        return "adminadd.html";
    }

    /**
     * 添加账号数据
     * @param username 用户名
     * @param email 电子邮箱，默认密码也是电子邮箱
     * @param phone 手机号码
     * @param rid 所属权限组id
     * @param images 本人图片
     * @throws IOException
     */
    @PostMapping("/adminaddData")
    @ResponseBody
    public String adminaddData(@RequestParam("name") String username, @RequestParam("email") String email,
                               @RequestParam("phone") Long phone, @RequestParam("rid") Long rid,
                               @RequestParam("image") MultipartFile images) throws IOException {
        //处理图片上传
        if(images.isEmpty()){
            return "添加用户失败！请核实输入信息时候符合规定或找技术人员处理！";
        }
        String newName = makeUtil.turnFileName(images.getOriginalFilename());
        images.transferTo(new File("F:\\OneDrive\\vhr_springboot_image\\user\\",newName));
        String image = "uimage/" + newName;

        String password = passwordEncoder.encode(email);
        LoginUser user = new LoginUser(0L,username,password,email,phone,image,1);
        Integer re = adminService.adminadd(user);
        if(re == 0){
            return "添加用户失败！请核实输入信息时候符合规定或找技术人员处理！";
        }
        Long uid = adminService.selectUidByPhone(phone);
        LoginUserRole loginUserRole = new LoginUserRole(0L,uid,rid);
        Integer result = adminService.userRoleAdd(loginUserRole);
        if(result == 1){
            return "添加用户成功！";
        }else {
            return "添加用户失败！请核实输入信息时候符合规定或找技术人员处理！";
        }
    }

    /**
     * 删除admin
     * @param id 账号的id
     */
    @PostMapping("/admindel")
    @ResponseBody
    public Integer admindel(@RequestParam("id") Long id,
                            @RequestParam("page") Integer page){
        //从user表删除账号，如成功删除则把user和role的连接表的该条数据一并删除
        //有一个删除失败都直接返回0.
        Integer re = adminService.admindel(id,page);
        Integer result = 0;
        if(re == 1){
            result = adminService.adminRoledel(id);
        }
        log.info("----删除该条成员数据{}-------",result);
        return result;
    }
}
