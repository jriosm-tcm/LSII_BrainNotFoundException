package com.tecnocampus.LS2.protube_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.users.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.profiles.active=dev",
    "ENV_PROTUBE_STORE_DIR=${java.io.tmpdir}/protube-auth-integration"
})
@AutoConfigureMockMvc
@Transactional
class AuthenticationFlowIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired ObjectMapper mapper;

    @Test void registersThenAuthenticatesAndRejectsWrongPassword() throws Exception {
        String email = "integration-" + java.util.UUID.randomUUID() + "@example.com";
        mvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(Map.of("email", email, "password", "testPassword123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
        mvc.perform(get("/api/auth/me").with(SecurityMockMvcRequestPostProcessors.httpBasic(email, "testPassword123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        mvc.perform(get("/api/auth/me").with(SecurityMockMvcRequestPostProcessors.httpBasic(email, "wrongPassword")))
                .andExpect(status().isUnauthorized());
    }
}
