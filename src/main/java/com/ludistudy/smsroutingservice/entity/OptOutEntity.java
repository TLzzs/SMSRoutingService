package com.ludistudy.smsroutingservice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class OptOutEntity {

    private String phoneNumber;
    private Instant optedOutAt;
}
