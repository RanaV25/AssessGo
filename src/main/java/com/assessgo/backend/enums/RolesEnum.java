package com.assessgo.backend.enums;


public enum RolesEnum {
    ADMIN(1, "Admin"),
    SUPER_ADMIN(2, "Super Admin"),
    USER(3, "User"),
    CONTRIBUTOR(3, "contributor");

    private int id;

    private String value;

    RolesEnum(int id, String value) {
        this.id = id;
        this.value = value;
    }

    public static String[] getAllRoles() {
        String[] roles = new String[RolesEnum.values().length];
        for (int i = 0; i < roles.length; i++) {
            roles[i] = RolesEnum.values()[i].getValue();
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
