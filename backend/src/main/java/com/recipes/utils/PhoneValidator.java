package com.recipes.utils;

public class PhoneValidator {
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    public static boolean validate(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return phone.matches(PHONE_REGEX);
    }
}
