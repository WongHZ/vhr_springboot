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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("vhr_device")
public class Device implements Serializable {
    @TableId("id")
    private Long did;

    @TableField("community_id")
    private Long cid;

    @TableField("code")
    private String code;

    @TableField("type")
    private String type;

    @TableField("name")
    private String name;

    @TableField("brand")
    private String brand;

    @TableField("unit_price")
    private BigDecimal price;

    @TableField("num")
    private Integer buynum;

    @TableField("expected_useful_life")
    private Integer eulife;

    @TableField("create_time")
    private Timestamp createtime;

    @TableField("update_time")
    private Timestamp updatetime;

    @TableLogic("is_deleted")
    private Integer is_deleted;

    private Community community;

    public Device(Long did, Long cid, String code, String type,
                  String name, String brand, BigDecimal price,
                  Integer buynum, Integer eulife, Timestamp createtime,Timestamp updatetime) {
        this.did = did;
        this.cid = cid;
        this.code = code;
        this.type = type;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.buynum = buynum;
        this.eulife = eulife;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }

    public Device(Long did, Long cid, String type,
                  String name, String brand, BigDecimal price,
                  Integer buynum, Integer eulife, Timestamp updatetime) {
        this.did = did;
        this.cid = cid;
        this.type = type;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.buynum = buynum;
        this.eulife = eulife;
        this.updatetime = updatetime;
    }
}
