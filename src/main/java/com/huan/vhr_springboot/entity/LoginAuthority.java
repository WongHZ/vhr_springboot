package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("authority")
public class LoginAuthority implements Serializable {
    @TableId("a_id")
    private Long aid;
    @TableField("a_name")
    private String aname;
    private String url;
    private Integer type;
    private String permission;
    private String method;
    private String description;
    private Integer status;
}
