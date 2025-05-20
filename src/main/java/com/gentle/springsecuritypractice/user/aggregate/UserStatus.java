package com.gentle.springsecuritypractice.user.aggregate;

public enum UserStatus {
    ACTIVE, INACTIVE, DELETED;

    public static boolean isValid(String value) {
        try {
            UserStatus.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
