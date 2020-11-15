package com.assessgo.backend.enums;

public enum RoleEnum {
    SUPER_ADMIN(1, "Super Admin"),
    ADMIN(2, "Admin"),
    USER(3, "User"),
    CONTRIBUTOR(3, "contributor");

    private Integer id;
    private String value;

    RoleEnum(Integer id, String value){
        this.id = id;
        this.value = value;
    }

    public static String[] getAllRoles() {
        String[] roles = new String[RoleEnum.values().length];
        for (int i = 0; i < roles.length; i++) {
            roles[i] = RoleEnum.values()[i].getValue();
        }
        return roles;
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
