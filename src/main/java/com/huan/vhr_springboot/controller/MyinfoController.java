package com.huan.vhr_springboot.controller;

import com.huan.vhr_springboot.entity.LoginUser;
import com.huan.vhr_springboot.service.AdminService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class MyinfoController {
    @Resource
    AdminService adminService;
    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    MakeUtil makeUtil;

    @GetMapping("/myinfo")
    public String myinfo(HttpServletRequest request,Model model){
        String head = request.getHeader("Cookie");
        String token = makeUtil.getToken(head);
        String uid = makeUtil.getUserData(token,"uid");
        String username = makeUtil.getUserData(token,"username");
        String image = makeUtil.getUserData(token,"image");
        String phone = makeUtil.getUserData(token,"phone");
        String email = makeUtil.getUserData(token,"email");
        Long nphone = Long.parseLong(phone);
        Long nuid = Long.parseLong(uid);
        LoginUser user = new LoginUser(nuid,username,null,email,nphone,image,0);
        model.addAttribute("user",user);
        return "myinfo.html";
    }

    @PostMapping("/changeinfo")
    @ResponseBody
    public Integer changeinfo(@RequestParam("name") String name,@RequestParam("phone") Long phone,
                              @RequestParam("email") String email,@RequestParam("uid") Long uid){
        LoginUser user = new LoginUser(uid,name,null,email,phone,null,null);
        Integer result = adminService.updateUser(user);
        return result;
    }

    @PostMapping("/passmatch")
    @ResponseBody
    public Integer passmatch(@RequestParam("name") String name,@RequestParam("phone") Long phone,
                             @RequestParam("email") String email,@RequestParam("uid") Long uid,
                             @RequestParam("opass") String opass){
        LoginUser user = adminService.selectDataByUidOrName(uid,null);
        boolean match = passwordEncoder.matches(opass, user.getPassword());
        if(match){
            return 1;
        }else {
            return 0;
        }
    }

    @PostMapping("/changepass")
    @ResponseBody
    public Integer changepass(@RequestParam("name") String name,@RequestParam("phone") Long phone,
                             @RequestParam("email") String email,@RequestParam("uid") Long uid,
                             @RequestParam("confirm") String npass){
        String pass = passwordEncoder.encode(npass);
        Integer result = adminService.updatePass(uid,pass);
        return result;
    }
}
