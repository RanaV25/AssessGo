package com.assessgo.backend.enums;


public enum AssessmentTypeEnum {
    ASSESSMENT(1, "Assessment"),
    SURVEY(2, "Survey"),
    QUIZ(3, "Quiz");

    private int id;

    private String value;

    AssessmentTypeEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static String[] getAllAssessmentTypes() {
        String[] assessment = new String[RolesEnum.values().length];
        for (int i = 0; i < assessment.length; i++) {
            assessment[i] = AssessmentTypeEnum.values()[i].getValue();
        }
        return assessment;
    }


    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }


}
