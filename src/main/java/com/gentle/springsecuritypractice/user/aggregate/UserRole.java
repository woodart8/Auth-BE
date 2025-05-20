package com.gentle.springsecuritypractice.user.aggregate;

public enum UserRole {
    ADMIN, ENTERPRISE;

    public static boolean isValid(String value) {
        try {
            UserRole.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
