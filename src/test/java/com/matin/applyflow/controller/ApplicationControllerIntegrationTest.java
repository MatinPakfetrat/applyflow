package com.matin.applyflow.controller;

import com.matin.applyflow.dto.ApplicationRequest;
import com.matin.applyflow.dto.RegisterRequest;
import com.matin.applyflow.model.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void registerAndLogin() throws Exception{
        RegisterRequest request = new RegisterRequest();
        request.setUsername("matin");
        request.setEmail("matin@email.com");
        request.setPassword("secret123");

        String response = mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(response).get("token").asString();
    }

    @Test
    void getApplications_withoutToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createApplication_withValidTokenAndData_returns201() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");
        request.setJobTitle("Backend Engineer");
        request.setSalary(new BigDecimal("130000"));
        request.setStatus(ApplicationStatus.APPLIED);

        mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("Google"));
    }


    @Test
    void getApplications_returnsOnlyOwnApplications() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Amazon");
        request.setJobTitle("SWE");
        request.setSalary(new BigDecimal("110000"));
        request.setStatus(ApplicationStatus.APPLIED);

        mockMvc.perform(post("/api/applications")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/applications")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("Amazon"));
    }
}
