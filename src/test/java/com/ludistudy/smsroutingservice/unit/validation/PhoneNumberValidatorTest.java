package com.ludistudy.smsroutingservice.unit.validation;

import com.ludistudy.smsroutingservice.exception.InvalidPhoneNumberException;
import com.ludistudy.smsroutingservice.validation.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.NZ_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.US_PHONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneNumberValidatorTest {

    private PhoneNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PhoneNumberValidator();
    }

    @Test
    void acceptsValidAustralianMobile() {
        assertEquals(AU_PHONE, validator.validateAndNormalize(AU_PHONE));
    }

    @Test
    void acceptsValidNewZealandMobile() {
        assertEquals(NZ_PHONE, validator.validateAndNormalize(NZ_PHONE));
    }

    @Test
    void normalizesWhitespace() {
        assertEquals(AU_PHONE, validator.validateAndNormalize("+61 491 570 156"));
    }

    @Test
    void normalizesAustralianLocalFormat() {
        assertEquals(AU_PHONE, validator.validateAndNormalize("0491570156"));
    }

    @Test
    void acceptsGlobalNumber() {
        assertEquals(US_PHONE, validator.validateAndNormalize(US_PHONE));
    }

    @Test
    void rejectsMissingPlus() {
        assertThrows(InvalidPhoneNumberException.class,
                () -> validator.validateAndNormalize("61491570156"));
    }

    @Test
    void rejectsAustralianWrongLength() {
        assertThrows(InvalidPhoneNumberException.class,
                () -> validator.validateAndNormalize("+6149157015"));
    }

    @Test
    void rejectsNonDigitCharacters() {
        assertThrows(InvalidPhoneNumberException.class,
                () -> validator.validateAndNormalize("+6149157015a"));
    }
}
