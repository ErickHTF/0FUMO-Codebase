package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RegistrarEventosIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    private String uniqueEmail() {
        return "teste+" + UUID.randomUUID() + "@zerofumo.com";
    }

    private Map<String, Object> registerUser() throws Exception {
        String payload = objectMapper.writeValueAsString(
                Map.of("name", "Teste User", "email", uniqueEmail(), "password", "senha1234"));

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return Map.of(
                "token", body.get("token").asText(),
                "userId", body.get("user").get("id").asLong()
        );
    }

    private String completeAssessment(String token, Long userId) throws Exception {
        String payload = objectMapper.writeValueAsString(
                Map.of("cigsPerDay", 10, "smokingYears", "1-5", "motivation", "health", "dependencyLevel", "medium"));

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        return token;
    }

    private String eventPayload(String type, String intensity, String trigger, String notes) throws Exception {
        Map<String, Object> map = new HashMap<>();
        if (type != null)      map.put("type", type);
        if (intensity != null) map.put("intensity", intensity);
        if (trigger != null)   map.put("trigger", trigger);
        if (notes != null)     map.put("notes", notes);
        map.put("occurredAt", "2026-06-04T10:30:00");
        return objectMapper.writeValueAsString(map);
    }

    // ------------------------------------------------------------------ //
    //  Fluxo principal                                                    //
    // ------------------------------------------------------------------ //

    @Test
    void registerEvent_shouldReturn201WithAllFieldsPersisted() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("fissura", "alta", "estresse", "Tive vontade após reunião")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event.id").isNumber())
                .andExpect(jsonPath("$.event.type").value("fissura"))
                .andExpect(jsonPath("$.event.intensity").value("alta"))
                .andExpect(jsonPath("$.event.trigger").value("estresse"))
                .andExpect(jsonPath("$.event.notes").value("Tive vontade após reunião"))
                .andExpect(jsonPath("$.event.occurredAt").isNotEmpty())
                .andExpect(jsonPath("$.suggestions").isArray());
    }

    @Test
    void registerEventWithoutNotes_shouldReturn201() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("resistiu", "media", "tedio", null)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event.type").value("resistiu"))
                .andExpect(jsonPath("$.suggestions").isArray());
    }

    @Test
    void listEvents_shouldReturnAllUserEvents() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("fissura", "baixa", "social", null)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // ------------------------------------------------------------------ //
    //  Validação de campos obrigatórios                                   //
    // ------------------------------------------------------------------ //

    @Test
    void registerEventWithoutBody_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerEventWithoutType_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload(null, "alta", "estresse", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").isNotEmpty());
    }

    @Test
    void registerEventWithoutIntensity_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("fissura", null, "estresse", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.intensity").isNotEmpty());
    }

    @Test
    void registerEventWithoutTrigger_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");
        completeAssessment(token, userId);

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("fissura", "alta", null, null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.trigger").isNotEmpty());
    }

    // ------------------------------------------------------------------ //
    //  Segurança                                                          //
    // ------------------------------------------------------------------ //

    @Test
    void registerEventWithoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("fissura", "alta", "estresse", null)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerEventBeforeAssessment_shouldReturn403() throws Exception {
        var session = registerUser();
        String token = (String) session.get("token");

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventPayload("fissura", "alta", "estresse", null)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
