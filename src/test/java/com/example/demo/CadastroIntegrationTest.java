package com.example.demo;

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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CadastroIntegrationTest {

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

    private static final String REGISTER_URL = "/api/auth/register";

    private String uniqueEmail() {
        return "teste+" + UUID.randomUUID() + "@zerofumo.com";
    }

    private String buildPayload(String name, String email, String password) throws Exception {
        return objectMapper.writeValueAsString(Map.of("name", name, "email", email, "password", password));
    }

    @Test
    void registerWithValidData_shouldReturn201WithToken() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("João Silva", uniqueEmail(), "senha1234")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.name").value("João Silva"))
                .andExpect(jsonPath("$.user.email").isNotEmpty());
    }

    @Test
    void registerWithMissingFields_shouldReturn400WithMessages() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Nome é obrigatório"))
                .andExpect(jsonPath("$.email").value("E-mail é obrigatório"))
                .andExpect(jsonPath("$.password").value("Senha é obrigatória"));
    }

    @Test
    void registerWithInvalidEmail_shouldReturn400() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("João", "emailinvalido", "senha1234")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("E-mail inválido"));
    }

    @Test
    void registerWithShortPassword_shouldReturn400() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("João", uniqueEmail(), "123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Senha deve ter no mínimo 8 caracteres"));
    }

    @Test
    void registerWithDuplicateEmail_shouldReturn409() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("João", email, "senha1234")))
                .andExpect(status().isCreated());

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("Maria", email, "senha5678")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString(email)));
    }

    @Test
    void registerWithDuplicateEmailDifferentCase_shouldReturn409() throws Exception {
        String email = uniqueEmail();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("João", email, "senha1234")))
                .andExpect(status().isCreated());

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("Maria", email.toUpperCase(), "senha5678")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void registerSuccess_shouldReturnAssessmentNotCompleted() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("Maria Silva", uniqueEmail(), "senha1234")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.assessmentCompleted").value(false));
    }

    @Test
    void accessFeaturesWithoutAssessment_shouldReturn403() throws Exception {
        MvcResult result = mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildPayload("Carlos", uniqueEmail(), "senha1234")))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/api/features")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }
}
