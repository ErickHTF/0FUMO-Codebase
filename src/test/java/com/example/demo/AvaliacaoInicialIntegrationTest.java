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

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AvaliacaoInicialIntegrationTest {

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

    /** Registra um usuário e retorna { token, userId }. */
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

    private String assessmentPayload(Integer cigsPerDay, String smokingYears,
                                     String motivation, String dependencyLevel) throws Exception {
        var map = new java.util.HashMap<String, Object>();
        if (cigsPerDay != null)     map.put("cigsPerDay", cigsPerDay);
        if (smokingYears != null)   map.put("smokingYears", smokingYears);
        if (motivation != null)     map.put("motivation", motivation);
        if (dependencyLevel != null) map.put("dependencyLevel", dependencyLevel);
        return objectMapper.writeValueAsString(map);
    }

    // ------------------------------------------------------------------ //
    //  Fluxo principal                                                    //
    // ------------------------------------------------------------------ //

    @Test
    void completeAssessment_shouldReturn200WithAllFieldsPersisted() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(10, "1-5", "health", "medium")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assessmentCompleted").value(true))
                .andExpect(jsonPath("$.cigsPerDay").value(10))
                .andExpect(jsonPath("$.smokingYears").value("1-5"))
                .andExpect(jsonPath("$.motivation").value("health"))
                .andExpect(jsonPath("$.dependencyLevel").value("medium"));
    }

    @Test
    void afterAssessment_featuresEndpointShouldBeAccessible() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(5, "6-10", "family", "high")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/features")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void beforeAssessment_featuresEndpointShouldReturn403() throws Exception {
        var session = registerUser();
        String token = (String) session.get("token");

        mockMvc.perform(get("/api/features")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    // ------------------------------------------------------------------ //
    //  Validação de campos obrigatórios                                   //
    // ------------------------------------------------------------------ //

    @Test
    void assessmentWithoutBody_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void assessmentWithoutCigsPerDay_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(null, "1-5", "health", "low")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cigsPerDay").isNotEmpty());
    }

    @Test
    void assessmentWithZeroCigs_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(0, "1-5", "health", "low")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cigsPerDay").isNotEmpty());
    }

    @Test
    void assessmentWithoutSmokingYears_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(10, null, "health", "low")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.smokingYears").isNotEmpty());
    }

    @Test
    void assessmentWithoutMotivation_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(10, "1-5", null, "low")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.motivation").isNotEmpty());
    }

    @Test
    void assessmentWithoutDependencyLevel_shouldReturn400() throws Exception {
        var session = registerUser();
        String token  = (String) session.get("token");
        Long   userId = (Long)   session.get("userId");

        mockMvc.perform(post("/api/users/{id}/assessment", userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(10, "1-5", "health", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.dependencyLevel").isNotEmpty());
    }

    // ------------------------------------------------------------------ //
    //  Segurança                                                          //
    // ------------------------------------------------------------------ //

    @Test
    void assessmentWithoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(post("/api/users/1/assessment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assessmentPayload(10, "1-5", "health", "medium")))
                .andExpect(status().isUnauthorized());
    }
}
