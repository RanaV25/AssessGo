package com.assessgo.backend.enums;

public enum UserValidation {
    USERNAME_REQUIRED(100),
    USERNAME_ALREADY_EXISTS(200),
    PASSWORD_NOT_FOUND(300),
    NEED_A_STRONG_PASSWORD(400),
    INVALID_EMAIL(500),
    FIRST_NAME_REQUIRED(600),
    LAST_NAME_REQUIRED(700),
    VALID_USER(0);


    private final int value;

    UserValidation(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
