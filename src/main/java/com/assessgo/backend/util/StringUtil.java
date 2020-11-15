package com.assessgo.backend.util;

public class StringUtil {
    public static final String REGEX_EMAIL = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public static boolean isValidEmail(String email) {
        return email.matches(REGEX_EMAIL);
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 6;
    }
}
