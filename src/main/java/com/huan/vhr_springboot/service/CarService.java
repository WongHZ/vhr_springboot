package com.huan.vhr_springboot.service;

import com.huan.vhr_springboot.entity.Car;

import java.util.List;

public interface CarService {
    List<Car> selectAllCar(Long page, Long houseId);
    Integer total(Long houseId);
    Integer pageNum(Integer total);
    Long selectCaridByCarnum(String carNum);
    Integer addCar(Car car);
    Car selectDataById(Long cid);
    Integer updateCar(Car car,Integer page);
    Integer cardel(Car car);
    Car selectCarBycarCode(String code);

}
