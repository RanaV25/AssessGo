package com.assessgo.backend.enums;


public enum QuestionTypesEnum {

    SCALE(0,"Scalable"),
    FREE_TEXT(1,"Free Text"),
    SINGLE_CHOICE(2,"Single Choice"),
    MULTIPLE_CHOICE(3,"Multiple Choice");

    private Integer id;
    private String value;

    QuestionTypesEnum(Integer id, String value){
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
