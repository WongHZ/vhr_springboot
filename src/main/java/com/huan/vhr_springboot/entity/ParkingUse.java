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
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("车位使用管理实体类")
@TableName("vhr_parking_use")
public class ParkingUse implements Serializable {
    @TableId
    @ApiModelProperty("数据id")
    private Long id;

    @TableField("community_id")
    @ApiModelProperty("小区id")
    private Long cid;

    @TableField("cname")
    @ApiModelProperty("小区姓名")
    private String cname;

    @TableField("parking_id")
    @ApiModelProperty("车位id")
    private Long parkingid;

    @TableField("parkcode")
    @ApiModelProperty("车位编号")
    private String parkcode;

    @TableField("car_id")
    @ApiModelProperty("车辆id")
    private Long carid;

    @TableField("carcode")
    @ApiModelProperty("车牌号")
    private String carcode;

    @TableField("cariamge")
    @ApiModelProperty("车辆图片")
    private String carimage;

    @TableField("personnel_id")
    @ApiModelProperty("成员id")
    private Long personnelid;

    @TableField("personnelname")
    @ApiModelProperty("车主姓名")
    private String personnelname;

    @TableField("phone")
    @ApiModelProperty("车主手机")
    private Long phone;

    @TableField("use_type")
    @ApiModelProperty("使用性质")
    private Integer usetype;

    @TableField("total_fee")
    @ApiModelProperty("总费用")
    private BigDecimal totalfee;

    @TableField("start_time")
    @ApiModelProperty("开始使用时间")
    private Date starttime;

    @TableField("end_time")
    @ApiModelProperty("截止使用时间")
    private Date endtime;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Timestamp createtime;

    @TableField("update_time")
    @ApiModelProperty("更新时间")
    private Timestamp updatetime;

    @TableLogic
    private Integer is_deleted;

    public ParkingUse(Long id, Long cid, String cname, Long parkingid,
                      String parkcode, Long carid, String carcode, String carimage,
                      Long personnelid, String personnelname, Long phone, Integer usetype,
                      BigDecimal totalfee, Date starttime, Date endtime,
                      Timestamp createtime, Timestamp updatetime) {
        this.id = id;
        this.cid = cid;
        this.cname = cname;
        this.parkingid = parkingid;
        this.parkcode = parkcode;
        this.carid = carid;
        this.carcode = carcode;
        this.carimage = carimage;
        this.personnelid = personnelid;
        this.personnelname = personnelname;
        this.phone = phone;
        this.usetype = usetype;
        this.totalfee = totalfee;
        this.starttime = starttime;
        this.endtime = endtime;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }

    public ParkingUse(Long id, Long cid, Long parkingid,
                      Long carid, Long personnelid, Integer usetype,
                      BigDecimal totalfee, Date starttime, Date endtime,
                      Timestamp createtime, Timestamp updatetime) {
        this.id = id;
        this.cid = cid;
        this.parkingid = parkingid;
        this.carid = carid;
        this.personnelid = personnelid;
        this.usetype = usetype;
        this.totalfee = totalfee;
        this.starttime = starttime;
        this.endtime = endtime;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }

    public ParkingUse(Long id, Long cid, Long parkingid, Long carid,
                      Long personnelid,Integer usetype, BigDecimal totalfee,
                      Date starttime, Date endtime, Timestamp updatetime) {
        this.id = id;
        this.cid = cid;
        this.parkingid = parkingid;
        this.carid = carid;
        this.personnelid = personnelid;
        this.usetype = usetype;
        this.totalfee = totalfee;
        this.starttime = starttime;
        this.endtime = endtime;
        this.updatetime = updatetime;
    }
}
