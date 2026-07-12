package com.ludistudy.smsroutingservice.service;

import com.ludistudy.smsroutingservice.model.Carrier;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selects carrier by destination prefix. AU numbers alternate Telstra/Optus on each send.
 * auCounter is thread-safe for concurrent HTTP requests within this JVM instance.
 */
@Service
public class CarrierRouter {

    // Even = Telstra, odd = Optus; resets on app restart (and in tests via reset())
    private final AtomicInteger auCounter = new AtomicInteger(0);

    public Carrier selectCarrier(String destinationNumber) {
        if (destinationNumber.startsWith("+61")) {
            return auCounter.getAndIncrement() % 2 == 0 ? Carrier.TELSTRA : Carrier.OPTUS;
        }
        if (destinationNumber.startsWith("+64")) {
            return Carrier.SPARK;
        }
        return Carrier.GLOBAL;
    }

    /** Resets AU alternation — used by tests to get deterministic carrier order. */
    public void reset() {
        auCounter.set(0);
    }
}
