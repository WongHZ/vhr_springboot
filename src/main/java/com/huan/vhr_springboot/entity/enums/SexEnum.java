package com.huan.vhr_springboot.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SexEnum {
    MALE(0,"男"),
    FEMALE(1,"女");

    @EnumValue
    private Integer sexNum;
    @JsonValue
    private String sexName;

    SexEnum(Integer sexNum, String sexName) {
        this.sexNum = sexNum;
        this.sexName = sexName;
    }

}
