package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Personnel;

import java.util.List;

public interface PersonnelService {
    List<Personnel> selectAllPersonnel(Long page, Long houseId);
    Integer total(Long houseId);
    Integer pageNum(Integer total);
    Integer personneldel(Long pid);
    Personnel selectDataByName(String name);
    Integer personneladd(Personnel personnel);
    Personnel selectByPid(Long pid);
    Integer personnelupdate(Personnel personnel);
    Integer totalTenant();
    String selectOwnerNameById(Long ownerId);
    Long selectOidByOname(String ownerName);
}
