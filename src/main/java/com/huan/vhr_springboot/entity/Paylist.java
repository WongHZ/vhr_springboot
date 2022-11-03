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
@TableName("vhr_paylist")
public class Paylist implements Serializable {

    @TableId("pid")
    private Long paid;

    @TableField("community_id")
    private Long cid;

    private String cname;

    @TableField("charge_id")
    private Long chid;

    private String chname;

    @TableField("person_id")
    private Long pid;

    private String pname;

    @TableField("admin_name")
    private String aname;

    @TableField("actual_money")
    private BigDecimal pay;

    private BigDecimal price;

    @TableField("description")
    private String descr;

    @TableField("create_time")
    private Timestamp createtime;

    @TableField("update_time")
    private Timestamp updatetime;

    @TableLogic("is_deleted")
    private Integer is_deleted;

    public Paylist(Long paid, Long cid, Long chid, Long pid, BigDecimal pay, String descr,String aname) {
        this.paid = paid;
        this.cid = cid;
        this.chid = chid;
        this.pid = pid;
        this.pay = pay;
        this.descr = descr;
        this.aname = aname;
    }

    public Paylist(Long paid,BigDecimal pay, String descr, Timestamp updatetime) {
        this.paid = paid;
        this.pay = pay;
        this.descr = descr;
        this.updatetime = updatetime;
    }
}
