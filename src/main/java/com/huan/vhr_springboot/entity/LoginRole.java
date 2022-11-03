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
@TableName("role")
public class LoginRole implements Serializable {
    @TableId("r_id")
    private Long rid;
    @TableField("r_name")
    private String rname;
    @TableField("creator_id")
    private Long cid;
    @TableField("create_time")
    private String ctime;
    @TableField("update_time")
    private String utime;
}
