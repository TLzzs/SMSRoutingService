package com.ludistudy.smsroutingservice.unit.service;

import com.ludistudy.smsroutingservice.entity.OptOutEntity;
import com.ludistudy.smsroutingservice.exception.InvalidPhoneNumberException;
import com.ludistudy.smsroutingservice.repository.OptOutRepository;
import com.ludistudy.smsroutingservice.service.OptOutService;
import com.ludistudy.smsroutingservice.validation.PhoneNumberValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.INVALID_PHONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OptOutServiceTest {

    @Mock
    private OptOutRepository optOutRepository;

    @Spy
    private PhoneNumberValidator phoneNumberValidator = new PhoneNumberValidator();

    @InjectMocks
    private OptOutService optOutService;

    @Test
    void optOut_savesNormalizedNumber() {
        when(optOutRepository.save(any(OptOutEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = optOutService.optOut(AU_PHONE);

        assertEquals(AU_PHONE, response.getPhoneNumber());
        assertTrue(response.isOptedOut());

        ArgumentCaptor<OptOutEntity> captor = ArgumentCaptor.forClass(OptOutEntity.class);
        verify(optOutRepository).save(captor.capture());
        assertEquals(AU_PHONE, captor.getValue().getPhoneNumber());
    }

    @Test
    void isOptedOut_delegatesToRepository() {
        when(optOutRepository.exists(AU_PHONE)).thenReturn(true);

        assertTrue(optOutService.isOptedOut(AU_PHONE));

        verify(optOutRepository).exists(AU_PHONE);
    }

    @Test
    void optOut_rejectsInvalidPhone() {
        assertThrows(InvalidPhoneNumberException.class, () -> optOutService.optOut(INVALID_PHONE));
        verify(optOutRepository, never()).save(any());
    }
}
