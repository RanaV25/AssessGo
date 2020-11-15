package com.assessgo.backend.enums;

import lombok.Getter;

@Getter
public enum AnswerLevelEnum {
    AWARENESS(20d,"Awareness"),
    BASIC(40d,"Basic"),
    DELIVERY(60d,"Delivery"),
    EXPERIENCE(80d, "Experience"),
    SHAPING(100d,"Shaping");


    double value;
    String level;

    AnswerLevelEnum(double value,String level) {
        this.value = value;
        this.level = level;
    }


}
