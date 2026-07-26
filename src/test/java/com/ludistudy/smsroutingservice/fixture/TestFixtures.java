package com.ludistudy.smsroutingservice.fixture;

import com.ludistudy.smsroutingservice.dto.SendMessageRequest;
import com.ludistudy.smsroutingservice.entity.MessageEntity;
import com.ludistudy.smsroutingservice.model.Carrier;
import com.ludistudy.smsroutingservice.model.MessageStatus;

import java.time.Instant;

public final class TestFixtures {

    public static final String AU_PHONE = "+61491570156";
    public static final String AU_PHONE_ALT = "+61491570157";
    public static final String NZ_PHONE = "+64211234567";
    public static final String US_PHONE = "+14155552671";
    public static final String INVALID_PHONE = "invalid";
    public static final String SMS_CHANNEL = "SMS";

    private TestFixtures() {
    }

    public static SendMessageRequest sendMessageRequest(String destinationNumber, String content) {
        SendMessageRequest request = new SendMessageRequest();
        request.setDestinationNumber(destinationNumber);
        request.setContent(content);
        request.setChannel(SMS_CHANNEL);
        return request;
    }

    public static SendMessageRequest australianSms(String content) {
        return sendMessageRequest(AU_PHONE, content);
    }

    public static String sendMessageJson(String destinationNumber, String content) {
        return """
                {
                  "destination_number": "%s",
                  "content": "%s",
                  "channel": "%s"
                }
                """.formatted(destinationNumber, content, SMS_CHANNEL);
    }

    public static String australianSmsJson(String content) {
        return sendMessageJson(AU_PHONE, content);
    }

    public static MessageEntity deliveredMessage(String id, String content) {
        return MessageEntity.builder()
                .id(id)
                .destinationNumber(AU_PHONE)
                .content(content)
                .channel(SMS_CHANNEL)
                .status(MessageStatus.DELIVERED)
                .carrier(Carrier.TELSTRA)
                .createdAt(Instant.parse("2026-07-10T00:00:00Z"))
                .updatedAt(Instant.parse("2026-07-10T00:00:01Z"))
                .build();
    }
}
