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
import java.sql.Timestamp;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("车位管理实体类")
@TableName("vhr_parking")
public class Parking implements Serializable {
    @TableId("id")
    @ApiModelProperty("实体类主键")
    private Long id;

    @TableField("community_id")
    @ApiModelProperty("用于绑定所属小区")
    private Long cid;

    @ApiModelProperty("用于显示所属小区")
    private String cname;

    @TableField("code")
    @ApiModelProperty("车位编号")
    private String code;

    @TableField("name")
    @ApiModelProperty("车位名称")
    private String name;

    @TableField("status")
    @ApiModelProperty("车位使用状态")
    private Integer status;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Timestamp createtime;

    @TableField("update_time")
    @ApiModelProperty("更新时间")
    private Timestamp updatetime;

    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Integer is_deleted;

    public Parking(Long id, Long cid, String code,
                   String name, Integer status, Timestamp createtime, Timestamp updatetime) {
        this.id = id;
        this.cid = cid;
        this.code = code;
        this.name = name;
        this.status = status;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }
}
