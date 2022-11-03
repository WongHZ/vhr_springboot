package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
@ApiModel("管理员信息表")
public class LoginUser implements Serializable {
    @TableId("u_id")
    @ApiModelProperty("表主键")
    private Long uid;
    @TableField("u_name")
    @ApiModelProperty("管理员姓名")
    private String uname;
    @ApiModelProperty("管理员密码")
    private String password;
    @ApiModelProperty("管理员邮箱")
    private String email;
    @ApiModelProperty("管理员手机")
    private Long phone;
    @ApiModelProperty("管理员头像")
    private String image;

    @ApiModelProperty("role对象，用于一对多的关系")
    private LoginRole role;
    @ApiModelProperty("账号状态")
    private Integer status;


    public LoginUser(Long uid, String uname, String password, String email, Long phone, String image,Integer status) {
        this.uid = uid;
        this.uname = uname;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.status = status;
    }
}
