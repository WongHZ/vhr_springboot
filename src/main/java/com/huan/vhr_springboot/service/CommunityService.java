package com.huan.vhr_springboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import com.huan.vhr_springboot.entity.Community;

import java.math.BigDecimal;
import java.util.List;

public interface CommunityService {
    IPage<Community> selectAllCommunity(Long pageNo,String admin);
    Integer insertCommunity(Community community);
    Integer updateCommunityById(Community community);
    Integer deleteCommunityById(Long id);
    BigDecimal countTotalBuildings();
    BigDecimal countTotalHouseholds();
    List<Community> selectAllCommunityName();
    Community selectDataByCidOrCname(Long cid,String cname);
}
