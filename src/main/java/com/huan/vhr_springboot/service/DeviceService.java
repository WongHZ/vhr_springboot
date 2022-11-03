package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Device;

import java.util.List;

public interface DeviceService {
    List<Device> selectAllDevice(Long page, Long cid);
    Integer total(Long cid);
    Integer pageNum(Integer total);
    Device selectDataBydidOrDname(Long did, Long cid,String dname);
    Integer addDevice(Device device);
    Integer upDevice(Device device,Integer page);
    Integer delDevice(Long did);
    List<Device> selectAllDeviceName(Long cid);
}
