package com.example.hw04_gymlog_v300;

public class LoginValidator {

    public boolean isValidUsername(String username) {
        return username != null && !username.trim().isEmpty();
    }

    public boolean isValidPassword(String enteredPassword, String storedPassword) {
        if (enteredPassword == null || storedPassword == null) {
            return false;
        }
        return enteredPassword.equals(storedPassword);
    }

    public boolean canLogin(String username, String enteredPassword, String storedPassword) {
        return isValidUsername(username) &&
                isValidPassword(enteredPassword, storedPassword);
    }
}