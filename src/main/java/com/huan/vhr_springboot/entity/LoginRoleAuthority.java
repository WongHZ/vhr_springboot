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
@TableName("role_authority")
public class LoginRoleAuthority implements Serializable {
    @TableId
    private Long id;
    @TableField("role_id")
    private Long rid;
    @TableField("authority_name")
    private String authname;
}
