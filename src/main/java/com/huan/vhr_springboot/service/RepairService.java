package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Repair;

import java.util.List;

public interface RepairService {
    List<Repair> selectAllRepair(Long page, String cname);
    Integer total(String cname);
    Integer pageNum(Integer total);
    Repair selectDataByridOrRname(Long rid, Long cid,String aname);
    Integer changeStatus(Long id,Integer status);
    Integer addRepair(Repair repair);
    Integer updateRepair(Repair repair);
    Integer delRepair(Long rid);
    List<Repair> welData();
}
