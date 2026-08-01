package com.matin.applyflow.controller;

import com.matin.applyflow.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_withValidData_returns201AndToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("matin");
        request.setEmail("matin@email.com");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    @Test
    void register_withDuplicateUsername_returns409() throws Exception{
        RegisterRequest first = new RegisterRequest();
        first.setUsername("matin");
        first.setEmail("matin@email.com");
        first.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated());

        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUsername("matin");
        duplicate.setEmail("different@email.com");
        duplicate.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_withWrongPassword_returns401() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setUsername("matin");
        request.setEmail("matin@email.com");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        String badLogin = """
                {"username": "matin", "password": "wrongpassword"}
                """;

        mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(badLogin))
                .andExpect(status().isUnauthorized());
    }
}
