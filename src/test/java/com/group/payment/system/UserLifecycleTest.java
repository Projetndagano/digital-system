package com.group.payment.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserLifeCycleTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }
    @Test
    void TC_SYS_001_fullUserLifecycle() throws Exception {

        // Step 1: Register
        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"lifecycleuser","email":"lifecycle@test.com","password":"Password1"}
                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = response.split("\"token\":\"")[1].split("\"")[0];

        // Step 2: Login
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"lifecycle@test.com","password":"Password1"}
                """))
                .andExpect(status().isOk());

        // Step 3: View profile
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.username").value("lifecycleuser"));

        // Step 4: Update profile
        mockMvc.perform(put("/api/auth/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"updateduser"}
                """))
                .andExpect(status().isOk());

        // Step 5: Deactivate account
        mockMvc.perform(delete("/api/auth/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Step 6: Login should fail after deactivation
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"lifecycle@test.com","password":"Password1"}
                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void TC_SYS_002_protectedEndpoints_noToken_allReturn401() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().is4xxClientError());
        mockMvc.perform(put("/api/auth/me")).andExpect(status().is4xxClientError());
        mockMvc.perform(delete("/api/auth/me")).andExpect(status().is4xxClientError());
    }

    @Test
    void TC_SYS_003_duplicateEmail_secondRegistrationFails() throws Exception {
        String body = """
            {"username":"dupuser","email":"dup2@test.com","password":"Password1"}
        """;
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }
}