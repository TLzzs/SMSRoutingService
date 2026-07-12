package com.ludistudy.smsroutingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank
    @JsonProperty("destination_number")
    private String destinationNumber;

    @NotBlank
    private String content;

    @NotBlank
    private String format;
}
