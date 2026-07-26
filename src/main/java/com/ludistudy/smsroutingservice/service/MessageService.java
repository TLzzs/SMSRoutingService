package com.ludistudy.smsroutingservice.service;

import com.ludistudy.smsroutingservice.dto.MessageResponse;
import com.ludistudy.smsroutingservice.dto.SendMessageRequest;
import com.ludistudy.smsroutingservice.entity.MessageEntity;
import com.ludistudy.smsroutingservice.exception.MessageNotFoundException;
import com.ludistudy.smsroutingservice.model.Carrier;
import com.ludistudy.smsroutingservice.model.MessageStatus;
import com.ludistudy.smsroutingservice.repository.MessageRepository;
import com.ludistudy.smsroutingservice.validation.PhoneNumberValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Core send/retrieve flow: validate phone, persist message, route to carrier or block if opted out.
 * Status transitions happen synchronously in one request (PENDING → SENT → DELIVERED, or PENDING → BLOCKED).
 */
@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final OptOutService optOutService;
    private final CarrierRouter carrierRouter;
    private final PhoneNumberValidator phoneNumberValidator;

    public MessageResponse send(SendMessageRequest request) {
        String destinationNumber = phoneNumberValidator.validateAndNormalize(request.getDestinationNumber());
        Instant now = Instant.now();

        MessageEntity message = MessageEntity.builder()
                .id(UUID.randomUUID().toString())
                .destinationNumber(destinationNumber)
                .content(request.getContent())
                .channel(request.getChannel())
                .status(MessageStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
        messageRepository.save(message);

        // Opt-out is checked after persist so blocked messages still have an ID for GET /messages/{id}
        if (optOutService.isOptedOut(destinationNumber)) {
            updateStatus(message, MessageStatus.BLOCKED, null);
            return MessageResponse.fromSend(message);
        }

        Carrier carrier = carrierRouter.selectCarrier(destinationNumber);
        // Simulates carrier handoff;
        updateStatus(message, MessageStatus.SENT, carrier);
        updateStatus(message, MessageStatus.DELIVERED, carrier);
        return MessageResponse.fromSend(message);
    }

    public MessageResponse getById(String id) {
        MessageEntity message = messageRepository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
        return MessageResponse.fromDetail(message);
    }

    private void updateStatus(MessageEntity message, MessageStatus status, Carrier carrier) {
        message.setStatus(status);
        if (carrier != null) {
            message.setCarrier(carrier);
        }
        message.setUpdatedAt(Instant.now());
        messageRepository.save(message);
    }
}
