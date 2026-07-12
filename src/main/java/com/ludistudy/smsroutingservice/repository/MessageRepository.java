package com.ludistudy.smsroutingservice.repository;

import com.ludistudy.smsroutingservice.entity.MessageEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory message store. Thread-safe for concurrent requests; data lost on restart. */
@Repository
public class MessageRepository {

    private final Map<String, MessageEntity> store = new ConcurrentHashMap<>();

    public MessageEntity save(MessageEntity message) {
        store.put(message.getId(), message);
        return message;
    }

    public Optional<MessageEntity> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    /** Clears all messages — test support only. */
    public void deleteAll() {
        store.clear();
    }
}
