package com.ludistudy.smsroutingservice.entity;

import com.ludistudy.smsroutingservice.model.Carrier;
import com.ludistudy.smsroutingservice.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    private String id;
    private String destinationNumber;
    private String content;
    private String channel;
    private MessageStatus status;
    private Carrier carrier;
    private Instant createdAt;
    private Instant updatedAt;
}
