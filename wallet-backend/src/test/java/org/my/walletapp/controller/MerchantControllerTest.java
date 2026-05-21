package org.my.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.my.walletapp.dto.merchant.MerchantRequest;
import org.my.walletapp.dto.merchant.MerchantResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.security.SecurityConfig;
import org.my.walletapp.service.merchant.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MerchantController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MerchantService merchantService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private User testUser;
    private MerchantResponse testResponse;
    private final Long userId = 1L;
    private final Long merchantId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testResponse = new MerchantResponse(merchantId, null, "Silpo", null, true);
    }

    @Test
    void getAllMerchants_ShouldReturn200AndList() throws Exception {
        when(merchantService.getAllMerchants(userId)).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/merchants")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Silpo"));
    }

    @Test
    void createMerchant_ShouldReturn201() throws Exception {
        MerchantRequest request = new MerchantRequest(null, "Novus", null);

        when(merchantService.createMerchant(eq(userId), any(MerchantRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/merchants")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Silpo"));
    }

    @Test
    void updateMerchantById_ShouldReturn200() throws Exception {
        MerchantRequest request = new MerchantRequest(null, "Updated Name", null);

        when(merchantService.updateMerchantById(eq(userId), eq(merchantId), any(MerchantRequest.class))).thenReturn(testResponse);

        mockMvc.perform(patch("/api/v1/merchants/{id}", merchantId)
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMerchantById_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/merchants/{id}", merchantId)
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getMerchantById_ShouldReturn200() throws Exception {
        when(merchantService.getMerchantById(userId, merchantId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/merchants/{id}", merchantId)
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(merchantId))
                .andExpect(jsonPath("$.name").value("Silpo"));
    }

    @Test
    void getMerchantById_ShouldReturn404_WhenNotFound() throws Exception {
        when(merchantService.getMerchantById(userId, 999L))
                .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/v1/merchants/{id}", 999L)
                        .with(user(testUser)))
                .andExpect(status().isNotFound());
    }
}
