package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
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
@TableName("vhr_community")
public class Community implements Serializable {
    @TableId("community_id")
    private Long cid;
    @TableField("community_code")
    private String c_code;
    @TableField("community_name")
    private String cname;
    @TableField("community_address")
    private String address;
    @TableField("community_area")
    private BigDecimal area;
    @TableField("total_buildings")
    private Integer totalbuildings;
    @TableField("total_households")
    private Integer totalhouses;
    private Integer greening_rate;
    @TableField("thumbnail")
    private String image;
    private String developer;
    private String estate_company;
    @TableField("create_time")
    private Timestamp createtime;
    @TableField("update_time")
    private Timestamp updatetime;
    private String status;
    private String manage;

    @TableLogic
    @ApiModelProperty("逻辑删除字段")
    private Integer is_deleted;

    public Community(Long cid, String c_code, String cname, String address, BigDecimal area,
                     Integer totalbuildings, Integer totalhouses, Integer greening_rate,
                     String image, String developer, String estate_company, Timestamp updatetime,
                     String status, String manage) {
        this.cid = cid;
        this.c_code = c_code;
        this.cname = cname;
        this.address = address;
        this.area = area;
        this.totalbuildings = totalbuildings;
        this.totalhouses = totalhouses;
        this.greening_rate = greening_rate;
        this.image = image;
        this.developer = developer;
        this.estate_company = estate_company;
        this.updatetime = updatetime;
        this.status = status;
        this.manage = manage;
    }

    public Community(Long cid, String c_code, String cname, String address, BigDecimal area,
                     Integer totalbuildings, Integer totalhouses, Integer greening_rate, String image,
                     String developer, String estate_company, Timestamp createtime, Timestamp updatetime,
                     String status, String manage) {
        this.cid = cid;
        this.c_code = c_code;
        this.cname = cname;
        this.address = address;
        this.area = area;
        this.totalbuildings = totalbuildings;
        this.totalhouses = totalhouses;
        this.greening_rate = greening_rate;
        this.image = image;
        this.developer = developer;
        this.estate_company = estate_company;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.status = status;
        this.manage = manage;
    }
}
