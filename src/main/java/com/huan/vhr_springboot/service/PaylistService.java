package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Paylist;

import java.util.List;

public interface PaylistService {
    List<Paylist> selectAllPaylist(Long page, String aname);
    Integer total(String aname);
    Integer pageNum(Integer total);
    Paylist selectDataByPaid(Long paid);
    Integer addPaylist(Paylist paylist);
    Integer upPaylist(Paylist paylist);
    Integer delPaylist(Long paid);
}
