package com.ludistudy.smsroutingservice.unit.service;

import com.ludistudy.smsroutingservice.model.Carrier;
import com.ludistudy.smsroutingservice.service.CarrierRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE_ALT;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.NZ_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.US_PHONE;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CarrierRouterTest {

    private CarrierRouter carrierRouter;

    @BeforeEach
    void setUp() {
        carrierRouter = new CarrierRouter();
        carrierRouter.reset();
    }

    @Test
    void alternatesAustralianCarriers() {
        assertEquals(Carrier.TELSTRA, carrierRouter.selectCarrier(AU_PHONE));
        assertEquals(Carrier.OPTUS, carrierRouter.selectCarrier(AU_PHONE_ALT));
        assertEquals(Carrier.TELSTRA, carrierRouter.selectCarrier("+61491570158"));
    }

    @Test
    void routesNewZealandToSpark() {
        assertEquals(Carrier.SPARK, carrierRouter.selectCarrier(NZ_PHONE));
    }

    @Test
    void routesOtherToGlobal() {
        assertEquals(Carrier.GLOBAL, carrierRouter.selectCarrier(US_PHONE));
    }
}
