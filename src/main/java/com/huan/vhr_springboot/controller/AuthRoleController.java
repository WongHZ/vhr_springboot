package com.huan.vhr_springboot.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.entity.LoginAuthority;
import com.huan.vhr_springboot.service.AuthRoleService;
import com.huan.vhr_springboot.util.MakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Controller
public class AuthRoleController {
    private static final Integer TYPE = 1;
    private static final String PERMISSION = "btn:admin";
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    @Resource
    AuthRoleService authRoleService;
    @Resource
    MakeUtil makeUtil;

    /**
     *
     * @param pageNo
     * @param model
     * @return
     */
    @GetMapping("/authrule")
    public String authrule(@RequestParam("page") Long pageNo, Model model){
        IPage<LoginAuthority> authoritylist = authRoleService.selectAllAuthRole(pageNo);
        //使用自定义工具类把总页数遍历出来变成list返回给前端用于翻页
        int p = (int) authoritylist.getPages();

        List<Integer> pagelist = makeUtil.turnlist(p);

        model.addAttribute("total",authoritylist.getTotal());
        model.addAttribute("total_count",pagelist);
        model.addAttribute("total_page",p);
        model.addAttribute("authority_list",authoritylist.getRecords());
        model.addAttribute("current",authoritylist.getCurrent());
        return "authrule.html";
    }

    /**
     * 更改该条数据状态
     * @param id 该条数据id
     * @param status 状态
     * @return 返回状态信息
     */
    @PostMapping("/authrulestatus")
    @ResponseBody
    public Integer changeStatus(@RequestParam("id") Long id,
                                @RequestParam("status") Integer status,
                                @RequestParam("page") Integer page){

        //id为1和10是超级管理员的权限，只供展示不能停用
        if(id == 1 || id == 10){
            return 2;
        }
        //如该条数据状态为1，则表示需要停用该账号，否则为启动该账号
        if(status == 1){
            status = 0;
        }else if(status == 0){
            status = 1;
        }
        Integer result = authRoleService.changeStatus(id,status,page);
        return result;
    }

    /**
     * 新增权限规则
     * @param model
     * @return
     */
    @GetMapping("/auth_ruleadd")
    public String authruleadd(Model model){
        return "auth_ruleadd.html";
    }

    /**
     * 添加权限数据
     * @param aname 权限名
     * @param url 权限规则路径
     * @param desp 描述
     * @return
     */
    @PostMapping("/auth_ruleaddData")
    @ResponseBody
    public Integer auth_ruleaddData(@RequestParam("aname") String aname,@RequestParam("url") String url,
                                    @RequestParam("condition") String desp){

        //判断此url是否已存在数据库，有则直接返回
        boolean hasUrl = authRoleService.selectHasUrlByLikeUrlAndAname(aname,url);
        if(! hasUrl){
            return 2;
        }

        //当url为添加和更新时需要有两个url，因此如果url2为空时是del
        StringBuilder url2 = new StringBuilder("/");
        if(url.endsWith("add")){
            url2.append(url).append("Data");
        }else if(url.endsWith("update")){
            url2.append(url, 0, url.lastIndexOf("update")).append("upData");
        }else if(url.endsWith("status"))
        //为路径前加上“/”，为描述加上完整的描述规则，如：普通管理员，权限名称的什么功能
        url = "/" + url;
        desp = "普通管理员，" + desp;

        if(! StrUtil.equals("/",url2.toString())){
            //先添加一个路径，为进入添加数据页面
            LoginAuthority authority = new LoginAuthority(0L,aname,url,TYPE,PERMISSION,METHOD_GET,desp,1);
            Integer result = authRoleService.authorityadd(authority);
            //添加提交表单的url，描述最终为：普通管理员，权限名称的什么功能_POST
            desp = desp + "_" + METHOD_POST;
            LoginAuthority authority1 = new LoginAuthority(0L,aname,url2.toString(),TYPE,PERMISSION,METHOD_POST,desp,1);
            Integer result1 = authRoleService.authorityadd(authority1);
            //如两个语句都返回1则成功，否则直接返回0
            if(result == result1){
                return 1;
            }else {
                return 0;
            }
        }else {
            //删除、修改状态权限的添加
            LoginAuthority authority = new LoginAuthority(0L,aname,url,TYPE,PERMISSION,METHOD_POST,desp,1);
            Integer result = authRoleService.authorityadd(authority);
            return result;
        }
    }

}
