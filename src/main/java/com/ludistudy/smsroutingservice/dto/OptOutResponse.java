package com.ludistudy.smsroutingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OptOutResponse {

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("opted_out")
    private boolean optedOut;
}
