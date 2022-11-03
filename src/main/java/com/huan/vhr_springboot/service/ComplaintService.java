package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Complaint;

import java.util.List;

public interface ComplaintService {
    List<Complaint> selectAllComplaint(Long page, String aname);
    Integer total(String aname);
    Integer totalNew();
    Integer pageNum(Integer total);
    Complaint selectDataByCid(Long cpid);
    Integer addComplaint(Complaint complaint);
    Integer changeStatus(Long cpid,Integer status);
    Integer upComplaint(Complaint complaint);
    Integer delComplaint(Long cpid);
}
