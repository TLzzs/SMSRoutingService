package com.ludistudy.smsroutingservice.validation;

import com.ludistudy.smsroutingservice.exception.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;

/**
 * Validates and normalizes phone numbers to E.164.
 * AU local numbers (starting with 0) are converted to +61.
 */
@Component
public class PhoneNumberValidator {

    public String validateAndNormalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new InvalidPhoneNumberException("Phone number is required");
        }

        String normalized = phoneNumber.replaceAll("\\s+", "");
        // e.g. 0491570156 → +61491570156
        if (normalized.startsWith("0")) {
            normalized = "+61" + normalized.substring(1);
        }

        if (!normalized.startsWith("+")) {
            throw new InvalidPhoneNumberException("Phone number must be in E.164 format starting with +");
        }

        String digits = normalized.substring(1);
        if (!digits.matches("\\d+")) {
            throw new InvalidPhoneNumberException("Phone number must contain only digits after the country code");
        }

        if (normalized.startsWith("+61")) {
            // +61 plus 9 subscriber digits (11 digits total after stripping +)
            if (digits.length() != 11) {
                throw new InvalidPhoneNumberException("Australian numbers must be +61 followed by 9 digits");
            }
            return normalized;
        }

        if (normalized.startsWith("+64")) {
            if (digits.length() < 10 || digits.length() > 11) {
                throw new InvalidPhoneNumberException("New Zealand numbers must be +64 followed by 8 or 9 digits");
            }
            return normalized;
        }

        if (digits.length() < 8 || digits.length() > 15) {
            throw new InvalidPhoneNumberException("Phone number must be a valid E.164 number with 8 to 15 digits");
        }

        return normalized;
    }
}
