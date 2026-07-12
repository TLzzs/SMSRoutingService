package com.ludistudy.smsroutingservice.exception;

public class MessageNotFoundException extends RuntimeException {

    public MessageNotFoundException(String id) {
        super("Message not found: " + id);
    }
}
