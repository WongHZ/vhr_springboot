package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("vhr_charge")
public class Charge implements Serializable {
    @TableId("charge_id")
    private Long chid;

    @TableField("community_id")
    private Long cid;

    private String cname;

    @TableField("code")
    private String code;

    @TableField("name")
    private String name;

    @TableField("money")
    private BigDecimal price;

    @TableField("create_time")
    private Timestamp createtime;

    @TableField("update_time")
    private Timestamp updatetime;

    @TableLogic("is_deleted")
    private Integer is_deleted;

    public Charge(Long chid, Long cid, String code, String name, BigDecimal price) {
        this.chid = chid;
        this.cid = cid;
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public Charge(Long chid, Long cid, String code, String name, BigDecimal price, Timestamp updatetime) {
        this.chid = chid;
        this.cid = cid;
        this.code = code;
        this.name = name;
        this.price = price;
        this.updatetime = updatetime;
    }
}
