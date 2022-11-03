package com.huan.vhr_springboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.entity.House;

import java.util.List;

public interface HouseService {
    IPage<House> selectAllHouse(Long pageNo, String ownerName);

    Integer deleteHouseById(Long id);

    Integer addHouse(House house);
    House selectHouseByHid(Long hid);
    Integer updateHouse(House house);
    Long selectHidByHcode(String hcode);
    Long selectHidByHname(String name);
}
