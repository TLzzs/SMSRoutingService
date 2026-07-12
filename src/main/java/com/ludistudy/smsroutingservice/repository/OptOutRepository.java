package com.ludistudy.smsroutingservice.repository;

import com.ludistudy.smsroutingservice.entity.OptOutEntity;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory opt-out registry keyed by normalized phone number. */
@Repository
public class OptOutRepository {

    private final Map<String, OptOutEntity> store = new ConcurrentHashMap<>();

    public OptOutEntity save(OptOutEntity entity) {
        store.put(entity.getPhoneNumber(), entity);
        return entity;
    }

    public boolean exists(String phoneNumber) {
        return store.containsKey(phoneNumber);
    }

    /** Clears all opt-outs — test support only. */
    public void deleteAll() {
        store.clear();
    }
}
