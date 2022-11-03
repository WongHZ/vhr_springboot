package com.huan.vhr_springboot.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PersonTypeEnum {
    RESIDENT(0,"住户"),
    FAMILY(1,"家庭成员"),
    TENANT(2,"租户");

    @EnumValue
    private Integer typeNum;
    private String typeName;

    PersonTypeEnum(Integer typeNum, String typeName) {
        this.typeNum = typeNum;
        this.typeName = typeName;
    }
}
