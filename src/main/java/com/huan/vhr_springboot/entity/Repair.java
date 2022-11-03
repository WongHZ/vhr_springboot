package com.huan.vhr_springboot.entity;

import com.baomidou.mybatisplus.annotation.*;
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
@TableName("vhr_repair")
public class Repair implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Long rid;

    @TableField("community_id")
    private Long cid;

    private String cname;

    @TableField("person_id")
    private Long pid;

    private String pname;

    @TableField("device_id")
    private Long did;

    private String dname;

    @TableField("admin_name")
    private String aname;

    @TableField("description")
    private String descr;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private Timestamp createtime;

    @TableField("update_time")
    private Timestamp updatetime;

    @TableLogic
    private Integer is_deleted;

    public Repair(Long rid,Long cid, Long pid, Long did, String aname, String descr) {
        this.rid = rid;
        this.cid = cid;
        this.pid = pid;
        this.did = did;
        this.aname = aname;
        this.descr = descr;
    }

    public Repair(Long rid, Long cid, Long pid, Long did, String aname, String descr, Timestamp updatetime) {
        this.rid = rid;
        this.cid = cid;
        this.pid = pid;
        this.did = did;
        this.aname = aname;
        this.descr = descr;
        this.updatetime = updatetime;
    }
}
