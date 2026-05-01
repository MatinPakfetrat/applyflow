package com.matin.applyflow.controller;

import com.matin.applyflow.exception.GlobalExceptionHandler;
import com.matin.applyflow.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ApplicationControllerJsonErrorTest {

    @Test
    void invalidEnumInRequestBodyReturnsBadRequest() throws Exception {
        ApplicationService applicationService = mock(ApplicationService.class);
        ApplicationController controller = new ApplicationController(applicationService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        mockMvc.perform(post("/api/applications")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "companyName": "Acme",
                                  "jobTitle": "Backend Dev",
                                  "salary": 100000,
                                  "status": "BAD_STATUS"
                                }
                                """))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
