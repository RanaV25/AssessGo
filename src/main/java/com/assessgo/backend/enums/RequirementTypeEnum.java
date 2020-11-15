package com.assessgo.backend.enums;


public enum RequirementTypeEnum {
    REQUIREMENT_TYPE_1(1,"Requirement_type_1"),
    REQUIREMENT_TYPE_2(2,"Requirement_type_2"),
    REQUIREMENT_TYPE_3(3,"Requirement_type_3");

    private Integer id;
    private String value;

    RequirementTypeEnum(Integer id, String value){
        this.id = id;
        this.value = value;
    }

    public static String[] getAllValues() {
        String[] types = new String[RequirementTypeEnum.values().length];
        for (int i = 0; i < types.length; i++) {
            types[i] = RequirementTypeEnum.values()[i].getValue();
        }
        return types;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString(){
        return value;
    }
}
