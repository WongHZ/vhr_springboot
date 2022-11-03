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
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("家庭车的实体类")
@TableName("vhr_car")
public class Car implements Serializable {
    @TableId
    private Long id;

    @TableField("house_id")
    @ApiModelProperty("房产id，用于绑定此车属于哪个家庭")
    private Long hid;

    @ApiModelProperty("房产名，用于绑定此车属于哪个家庭")
    private String hname;

    @ApiModelProperty("房产号，用于绑定此车属于哪个家庭")
    private String hcode;

    @TableField("car_number")
    @ApiModelProperty("车牌号码")
    private String carnum;

    @TableField("color")
    @ApiModelProperty("车辆颜色")
    private String color;

    @TableField("picture")
    @ApiModelProperty("车辆图片")
    private String image;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Timestamp createtime;

    @TableField("update_time")
    @ApiModelProperty("更新时间")
    private Timestamp updatetime;

    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Integer is_deleted;

    public Car(Long id, Long hid, String carnum, String color, String image,
               Timestamp createtime, Timestamp updatetime, Integer is_deleted) {
        this.id = id;
        this.hid = hid;
        this.carnum = carnum;
        this.color = color;
        this.image = image;
        this.createtime = createtime;
        this.updatetime = updatetime;
        this.is_deleted = is_deleted;
    }

    public Car(Long id, Long hid, String carnum,
               String color, String image, Timestamp updatetime, Integer is_deleted) {
        this.id = id;
        this.hid = hid;
        this.carnum = carnum;
        this.color = color;
        this.image = image;
        this.updatetime = updatetime;
        this.is_deleted = is_deleted;
    }
}
