package me.ronygomes.userManagement.common.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import me.ronygomes.userManagement.common.exception.ValidationException;

import java.time.LocalDate;
import java.time.Period;

public class ValidationUtils {

    public static String normalizeEmail(String email) {
        if (email == null)
            return null;
        String[] parts = email.trim().toLowerCase().split("@");
        if (parts.length != 2)
            return email.trim().toLowerCase();
        return parts[0] + "@" + parts[1];
    }

    public static String formatPhoneNumber(String phoneNumber, String defaultCountry) {
        if (phoneNumber == null || phoneNumber.isBlank())
            return null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phone = phoneUtil.parse(phoneNumber, defaultCountry);
            if (!phoneUtil.isValidNumber(phone)) {
                throw new ValidationException("Invalid phone number");
            }

            return phoneUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new ValidationException("Unable to parse phone number");
        }
    }

    public static boolean isOldEnough(LocalDate dob, int minAge) {
        if (dob == null)
            return true;
        return Period.between(dob, LocalDate.now()).getYears() >= minAge;
    }

    public static boolean isValidName(String name) {
        if (name == null || name.length() < 2 || name.length() > 50)
            return false;
        return name.matches("^[a-zA-Z\\s\\-\\']+$");
    }
}
