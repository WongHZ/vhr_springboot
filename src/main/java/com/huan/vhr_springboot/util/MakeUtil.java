package com.huan.vhr_springboot.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

@Component
public class MakeUtil {
    private static final Integer MAXTIME = 180;

    /**
     * 将总页数转换成list然后传回前端用于遍历
     * @param total 总页数
     * @return list
     */
    public List<Integer> turnlist(int total){
        int i = 1;
        List<Integer> list = new ArrayList<>();
        while(i <= total){
            list.add(i);
            i++;
        }
        return list;
    }

    /**
     * 处理图片名称，全新名称为原名称-UUID-图片格式，步骤为
     * 1、首先把原名称截出来，即从第0个字符开始到最后一个“.”
     * 2、拼接一段UUID并转string类型
     * 3、拼接最后一个“.”后面的字符串，即图片格式
     * @param oname 旧名称
     * @return 新名称
     */
    public String turnFileName(String oname){
        String newName = oname.substring(0,oname.lastIndexOf(".")) + "-" +
                UUID.randomUUID() + oname.substring(oname.lastIndexOf("."));
        return newName;
    }

    /**
     * 返回一个固定时间再加一个3分钟内的随机时间
     * 用于设置redis存储的过期时间，降低雪崩发生的分享
     */
    public Integer redisttl(Integer ttl){
        Random random = new Random();
        return ttl + random.nextInt(MAXTIME);
    }

    /**
     * 将String转换成sql的date类型
     */
    public Date turnDate(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date d = null;
        try {
            d = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Date newDate = new java.sql.Date(d.getTime());
        return newDate;
    }

    public String getToken(String head){
        String header = head.substring(head.indexOf("Authorization="));
        String header2 = header.replace("Authorization=","");
        String token = header2.substring(7);
        return token;
    }

    public String getUserData(String token,String info){
        JWT jwt = JWTUtil.parseToken(token);
        String data = (String) jwt.getPayload().getClaim(info);
        return data;
    }
}
