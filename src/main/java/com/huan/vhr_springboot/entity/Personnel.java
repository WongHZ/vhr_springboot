package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huan.vhr_springboot.entity.enums.PersonTypeEnum;
import com.huan.vhr_springboot.entity.enums.SexEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("vhr_personnel")
@ApiModel(value = "人员管理管理实体类")
public class Personnel implements Serializable {
    @TableId("p_id")
    @ApiModelProperty("业主id，主键")
    private Long pid;

    @TableField("community_id")
    @ApiModelProperty("所属小区号")
    private Long cid;

    @ApiModelProperty("多表查询后的小区名，用于展示使用")
    private String cname;
    @ApiModelProperty("多表查询后的房产名，用于展示使用")
    private String hname;

    @TableField("house_id")
    @ApiModelProperty("所属房产id")
    private Long hid;

    @TableField("name")
    @ApiModelProperty("业主姓名")
    private String pname;

    @TableField("picture")
    @ApiModelProperty("业主照片")
    private String image;

    @TableField("idcard")
    @ApiModelProperty("业主身份证")
    private String idCard;

    @TableField("telephone")
    @ApiModelProperty("业主电话")
    private Long phone;

    @TableField("sex")
    @ApiModelProperty("业主性别，男/女")
    private SexEnum sex;

    @TableField("birthday")
    @ApiModelProperty("业主出生日期")
    private Date birthday;

    @TableField("type")
    @ApiModelProperty("业主类型，房主/租客")
    private PersonTypeEnum type;

    @TableField("create_time")
    @ApiModelProperty("创建时间")
    private Timestamp createtime;

    @TableField("update_time")
    @ApiModelProperty("更新时间")
    private Timestamp updatetime;

    @TableLogic
    @ApiModelProperty("逻辑删除字段")
    private Integer is_deleted;

    public Personnel(Long pid, String cname, String hname, String pname,
                     String image, String idCard, Long phone, SexEnum sex,
                     Date birthday, PersonTypeEnum type, Timestamp createtime,
                     Timestamp updatetime) {
        this.pid = pid;
        this.cname = cname;
        this.hname = hname;
        this.pname = pname;
        this.image = image;
        this.idCard = idCard;
        this.phone = phone;
        this.sex = sex;
        this.birthday = birthday;
        this.type = type;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }

    public Personnel(Long pid, Long cid, Long hid, String pname,
                     String image, String idCard, Long phone,
                     SexEnum sex, Date birthday, PersonTypeEnum type,
                     Timestamp updatetime) {
        this.pid = pid;
        this.cid = cid;
        this.hid = hid;
        this.pname = pname;
        this.image = image;
        this.idCard = idCard;
        this.phone = phone;
        this.sex = sex;
        this.birthday = birthday;
        this.type = type;
        this.updatetime = updatetime;
    }

    public Personnel(Long pid, Long cid, String cname, String hname, Long hid,
                     String pname, String image, String idCard, Long phone,
                     SexEnum sex, Date birthday, PersonTypeEnum type,
                     Timestamp createtime, Timestamp updatetime) {
        this.pid = pid;
        this.cid = cid;
        this.cname = cname;
        this.hname = hname;
        this.hid = hid;
        this.pname = pname;
        this.image = image;
        this.idCard = idCard;
        this.phone = phone;
        this.sex = sex;
        this.birthday = birthday;
        this.type = type;
        this.createtime = createtime;
        this.updatetime = updatetime;
    }
}
