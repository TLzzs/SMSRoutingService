package com.ludistudy.smsroutingservice.service;

import com.ludistudy.smsroutingservice.dto.OptOutResponse;
import com.ludistudy.smsroutingservice.entity.OptOutEntity;
import com.ludistudy.smsroutingservice.repository.OptOutRepository;
import com.ludistudy.smsroutingservice.validation.PhoneNumberValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/** Registers and checks phone numbers that must not receive messages. */
@Service
@AllArgsConstructor
public class OptOutService {

    private final OptOutRepository optOutRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    public OptOutResponse optOut(String phoneNumber) {
        String normalized = phoneNumberValidator.validateAndNormalize(phoneNumber);
        OptOutEntity entity = new OptOutEntity();
        entity.setPhoneNumber(normalized);
        entity.setOptedOutAt(Instant.now());
        optOutRepository.save(entity);
        return OptOutResponse.builder()
                .phoneNumber(normalized)
                .optedOut(true)
                .build();
    }

    public boolean isOptedOut(String phoneNumber) {
        return optOutRepository.exists(phoneNumber);
    }
}
