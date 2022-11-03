package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.ParkingUse;

import java.util.List;

public interface ParkingUseService {
    List<ParkingUse> selectAllParkingUse(Long page, Long cid);
    Integer total(Long cid);
    Integer pageNum(Integer total);
    Long selectIdBycarId(Long carId);
    Integer addParkingUse(ParkingUse parkingUse);
    ParkingUse selectDataById(Long id);
    Integer upParkingUse(ParkingUse parkingUse);
    Integer parkingusedel(Long id);
}
