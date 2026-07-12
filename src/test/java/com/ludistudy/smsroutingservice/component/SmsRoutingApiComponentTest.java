package com.ludistudy.smsroutingservice.component;

import com.jayway.jsonpath.JsonPath;
import com.ludistudy.smsroutingservice.repository.MessageRepository;
import com.ludistudy.smsroutingservice.repository.OptOutRepository;
import com.ludistudy.smsroutingservice.service.CarrierRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.AU_PHONE_ALT;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.INVALID_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.NZ_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.US_PHONE;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.australianSmsJson;
import static com.ludistudy.smsroutingservice.fixture.TestFixtures.sendMessageJson;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SmsRoutingApiComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private OptOutRepository optOutRepository;

    @Autowired
    private CarrierRouter carrierRouter;

    @BeforeEach
    void cleanDb() {
        messageRepository.deleteAll();
        optOutRepository.deleteAll();
        carrierRouter.reset();
    }

    @Test
    void postMessage_returnsDelivered() throws Exception {
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(australianSmsJson("Hello")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DELIVERED"))
                .andExpect(jsonPath("$.carrier").value("Telstra"));
    }

    @Test
    void getMessage_returnsStoredRecord() throws Exception {
        MvcResult sendResult = mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(australianSmsJson("Hello world")))
                .andExpect(status().isCreated())
                .andReturn();

        String id = JsonPath.read(sendResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/messages/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.content").value("Hello world"))
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void postMessage_blockedAfterOptOut() throws Exception {
        mockMvc.perform(post("/optout/" + AU_PHONE))
                .andExpect(status().isOk());

        MvcResult sendResult = mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(australianSmsJson("Blocked")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.carrier").doesNotExist())
                .andReturn();

        String id = JsonPath.read(sendResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/messages/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").value("BLOCKED"))
                .andExpect(jsonPath("$.carrier").doesNotExist());
    }

    @Test
    void postMessage_rejectsEmptyBody() throws Exception {
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postMessage_rejectsInvalidPhone() throws Exception {
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sendMessageJson(INVALID_PHONE, "Hello")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMessage_returns404() throws Exception {
        mockMvc.perform(get("/messages/missing-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void australianCarriersAlternate() throws Exception {
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(australianSmsJson("One")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.carrier").value("Telstra"));

        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sendMessageJson(AU_PHONE_ALT, "Two")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.carrier").value("Optus"));
    }

    @Test
    void routesNewZealandToSpark() throws Exception {
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sendMessageJson(NZ_PHONE, "Kia ora")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.carrier").value("Spark"));
    }

    @Test
    void routesGlobalCarrier() throws Exception {
        mockMvc.perform(post("/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sendMessageJson(US_PHONE, "Hello")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.carrier").value("Global"));
    }
}
