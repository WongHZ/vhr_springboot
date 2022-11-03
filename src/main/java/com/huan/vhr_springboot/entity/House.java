package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("vhr_house")
@ApiModel(value = "个人房产管理实体类")
public class House implements Serializable {
    @ApiModelProperty("房产id，主键")
    @TableId("h_id")
    private Long hId;

    @ApiModelProperty("所属小区id")
    @TableField("community_id")
    private Long cId;

    @ApiModelProperty("所属小区名")
    @TableField("community_name")
    private String cName;

    @ApiModelProperty("所属楼名")
    @TableField("building_name")
    private String bName;

    @ApiModelProperty("房产编码")
    @TableField("h_code")
    private String hCode;

    @ApiModelProperty("房产名称")
    @TableField("h_name")
    private String hName;

    @ApiModelProperty("业主id")
    @TableField("owner_id")
    private Long oId;

    @ApiModelProperty("业主姓名")
    @TableField("owner_name")
    private String oName;

    @ApiModelProperty("业主电话")
    @TableField("telephone")
    private Long oPhone;

    @ApiModelProperty("房间数量")
    @TableField("room_num")
    private Integer roomNum;

    @ApiModelProperty("所在单元")
    @TableField("unit")
    private Integer unit;

    @ApiModelProperty("所在楼层")
    @TableField("floor")
    private Integer floor;

    @ApiModelProperty("入住时间")
    @TableField("live_time")
    private Date liveTime;

    @TableLogic
    @ApiModelProperty("逻辑删除字段")
    private Integer is_deleted;

    public House(Long hId, Long cId, String cName, String bName, String hCode, String hName,
                 Long oId, String oName, Long oPhone, Integer roomNum, Integer unit,
                 Integer floor, Date liveTime) {
        this.hId = hId;
        this.cId = cId;
        this.cName = cName;
        this.bName = bName;
        this.hCode = hCode;
        this.hName = hName;
        this.oId = oId;
        this.oName = oName;
        this.oPhone = oPhone;
        this.roomNum = roomNum;
        this.unit = unit;
        this.floor = floor;
        this.liveTime = liveTime;
    }
}
