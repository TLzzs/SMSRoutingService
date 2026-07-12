package com.ludistudy.smsroutingservice.unit.service;

import com.ludistudy.smsroutingservice.dto.SendMessageRequest;
import com.ludistudy.smsroutingservice.exception.InvalidPhoneNumberException;
import com.ludistudy.smsroutingservice.exception.MessageNotFoundException;
import com.ludistudy.smsroutingservice.model.Carrier;
import com.ludistudy.smsroutingservice.model.MessageStatus;
import com.ludistudy.smsroutingservice.repository.MessageRepository;
import com.ludistudy.smsroutingservice.service.CarrierRouter;
import com.ludistudy.smsroutingservice.service.MessageService;
import com.ludistudy.smsroutingservice.service.OptOutService;
import com.ludistudy.smsroutingservice.validation.PhoneNumberValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.INVALID_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.australianSms;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.deliveredMessage;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.sendMessageRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private OptOutService optOutService;

    @Mock
    private CarrierRouter carrierRouter;

    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    @InjectMocks
    private MessageService messageService;

    @Test
    void send_deliversWithCarrier() {
        when(phoneNumberValidator.validateAndNormalize(anyString())).thenReturn(AU_PHONE);
        when(optOutService.isOptedOut(AU_PHONE)).thenReturn(false);
        when(carrierRouter.selectCarrier(AU_PHONE)).thenReturn(Carrier.TELSTRA);
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = messageService.send(australianSms("Hello"));

        assertEquals(MessageStatus.DELIVERED, response.getStatus());
        assertEquals("Telstra", response.getCarrier());
        assertEquals(AU_PHONE, response.getDestinationNumber());
        verify(messageRepository, atLeastOnce()).save(any());
    }

    @Test
    void send_blocksOptedOutNumber() {
        when(phoneNumberValidator.validateAndNormalize(anyString())).thenReturn(AU_PHONE);
        when(optOutService.isOptedOut(AU_PHONE)).thenReturn(true);
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = messageService.send(australianSms("Hello"));

        assertEquals(MessageStatus.BLOCKED, response.getStatus());
        assertNull(response.getCarrier());
        verify(carrierRouter, never()).selectCarrier(anyString());
    }

    @Test
    void send_rejectsInvalidPhone() {
        when(phoneNumberValidator.validateAndNormalize(anyString()))
                .thenThrow(new InvalidPhoneNumberException("invalid"));

        SendMessageRequest request = sendMessageRequest(INVALID_PHONE, "Hello");

        assertThrows(InvalidPhoneNumberException.class, () -> messageService.send(request));
        verify(messageRepository, never()).save(any());
    }

    @Test
    void getById_returnsMessage() {
        when(messageRepository.findById("msg-1"))
                .thenReturn(Optional.of(deliveredMessage("msg-1", "Hello")));

        var response = messageService.getById("msg-1");

        assertEquals("msg-1", response.getId());
        assertEquals("Hello", response.getContent());
        assertEquals(MessageStatus.DELIVERED, response.getStatus());
        assertEquals("Telstra", response.getCarrier());
    }

    @Test
    void getById_throwsWhenMissing() {
        when(messageRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(MessageNotFoundException.class, () -> messageService.getById("missing"));
    }
}
