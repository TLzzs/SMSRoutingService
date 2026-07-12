package com.ludistudy.smsroutingservice.controller;

import com.ludistudy.smsroutingservice.dto.MessageResponse;
import com.ludistudy.smsroutingservice.dto.SendMessageRequest;
import com.ludistudy.smsroutingservice.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponse send(@Valid @RequestBody SendMessageRequest request) {
        return messageService.send(request);
    }

    @GetMapping("/{id}")
    public MessageResponse getById(@PathVariable String id) {
        return messageService.getById(id);
    }
}
