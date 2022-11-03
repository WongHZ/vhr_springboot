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
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@TableName("vhr_complaint")
public class Complaint implements Serializable {

    @TableId("complaint_id")
    private Long cpid;

    @TableField("community_id")
    private Long cid;

    private String cname;

    @TableField("person_id")
    private Long pid;

    private String pname;

    @TableField("description")
    private String descr;

    @TableField("create_time")
    private Timestamp createtime;

    @TableField("update_time")
    private Timestamp updatetime;

    @TableField("status")
    private Integer status;

    @TableField("admin_name")
    private String aname;

    @TableLogic("is_deleted")
    private Integer is_deleted;


    public Complaint(Long cpid, Long cid, Long pid, String descr, String aname) {
        this.cpid = cpid;
        this.cid = cid;
        this.pid = pid;
        this.descr = descr;
        this.aname = aname;
    }

    public Complaint(Long cpid, Long cid, Long pid, String descr, Timestamp updatetime) {
        this.cpid = cpid;
        this.cid = cid;
        this.pid = pid;
        this.descr = descr;
        this.updatetime = updatetime;
    }
}
