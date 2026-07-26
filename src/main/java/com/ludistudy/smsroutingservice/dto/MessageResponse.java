package com.ludistudy.smsroutingservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ludistudy.smsroutingservice.entity.MessageEntity;
import com.ludistudy.smsroutingservice.model.Carrier;
import com.ludistudy.smsroutingservice.model.MessageStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {

    private String id;

    @JsonProperty("destination_number")
    private String destinationNumber;

    private String content;
    private String channel;
    private MessageStatus status;
    private String carrier;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    /** Minimal fields returned immediately after POST /messages. */
    public static MessageResponse fromSend(MessageEntity message) {
        return MessageResponse.builder()
                .id(message.getId())
                .destinationNumber(message.getDestinationNumber())
                .status(message.getStatus())
                .carrier(carrierDisplayName(message.getCarrier()))
                .build();
    }

    /** Full record returned by GET /messages/{id}. */
    public static MessageResponse fromDetail(MessageEntity message) {
        return MessageResponse.builder()
                .id(message.getId())
                .destinationNumber(message.getDestinationNumber())
                .content(message.getContent())
                .channel(message.getChannel())
                .status(message.getStatus())
                .carrier(carrierDisplayName(message.getCarrier()))
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }

    private static String carrierDisplayName(Carrier carrier) {
        return carrier == null ? null : carrier.getDisplayName();
    }
}
