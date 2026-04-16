package me.ronygomes.userManagement.common.utils;

import me.ronygomes.userManagement.common.exception.ValidationException;

import java.util.Set;

public class PasswordValidator {

    private static final Set<String> WEAK_PASSWORDS = Set.of("1234567890", "password123", "qwertyuiop");

    public static void validate(String password, String emailPrefix, String phoneSubstring) {
        if (password == null || password.length() < 10) {
            throw new ValidationException("Password must be at least 10 characters long");
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;':\",.<>/?".indexOf(c) != -1);

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            throw new ValidationException("Password must contain uppercase, lowercase, digit, and special character");
        }

        if (WEAK_PASSWORDS.contains(password.toLowerCase())) {
            throw new ValidationException("Password is too common/weak");
        }

        if (emailPrefix != null && !emailPrefix.isBlank() && password.contains(emailPrefix)) {
            throw new ValidationException("Password must not contain email prefix");
        }

        if (phoneSubstring != null && !phoneSubstring.isBlank() && password.contains(phoneSubstring)) {
            throw new ValidationException("Password must not contain parts of phone number");
        }
    }
}
