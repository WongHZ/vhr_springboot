package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Parking;

import java.util.List;

public interface ParkingService {
    List<Parking> selectAllParking(Long page, Long cid);
    Integer total(Long cid);
    Integer pageNum(Integer total);
    Integer changeStatus(Long id,Integer status);
    Integer parkingdel(Long id);
    Integer parkingadd(Parking parking);
    Parking selectParkingByCarcode(String carCode);
    Long selectParkingByparkingcode(String parkingCode);
}
