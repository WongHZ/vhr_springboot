package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Charge;

import java.util.List;

public interface ChargeService {
    List<Charge> selectAllCharge(Long page, Long chid);
    Integer total(Long chid);
    Integer pageNum(Integer total);
    Charge selectDataByChidOrCode(Long chid,String code,String name);
    Integer addCharge(Charge charge);
    Integer upCharge(Charge charge);
    Integer delCharge(Long chid);
    List<Charge> selectDataByCid(Long cid);

}
