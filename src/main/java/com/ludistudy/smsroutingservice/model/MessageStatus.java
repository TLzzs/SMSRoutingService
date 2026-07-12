package com.ludistudy.smsroutingservice.model;

/** Message lifecycle: PENDING → SENT → DELIVERED (success) or PENDING → BLOCKED (opt-out). */
public enum MessageStatus {
    PENDING,
    SENT,
    DELIVERED,
    BLOCKED
}
