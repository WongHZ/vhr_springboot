package com.huan.vhr_springboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huan.vhr_springboot.entity.Building;

public interface BuildingService {
    IPage<Building> selectAllBuilding(Long pageNo, String buildingName);
    Integer addBuilding(Building building);
    Integer delBuilding(Long id);
    Integer delByCommunityName(String name);
    Integer updateBuildingNameByCommunityName(String name,Integer total);
}
