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
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("vhr_building")
public class Building implements Serializable {
    @TableId
    @ApiModelProperty("id，主键")
    private Long id;

    @TableField("community_id")
    @ApiModelProperty("小区id")
    private Long cid;

    @TableField("community_name")
    @ApiModelProperty("小区名")
    private String cname;

    @TableField("name")
    @ApiModelProperty("楼号")
    private String name;

    @TableField("total_households")
    @ApiModelProperty("总户数")
    private Integer totalhouses;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Timestamp createtime;

    @TableField("update_time")
    @ApiModelProperty("更新时间")
    private Timestamp updatetime;

    @TableLogic
    @ApiModelProperty("逻辑删除字段")
    private Integer is_deleted;

    public Building(Long id, Long cid, String cname, String name, Integer totalhouses,
                    Timestamp createtime, Timestamp updatetime) {
        this.id = id;
        this.cid = cid;
        this.cname = cname;
        this.name = name;
        this.totalhouses = totalhouses;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }
}
