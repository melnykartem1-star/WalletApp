package org.my.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.my.walletapp.security.SecurityConfig;
import org.my.walletapp.dto.auth.AuthResponse;
import org.my.walletapp.dto.auth.LoginRequest;
import org.my.walletapp.dto.auth.RegisterRequest;
import org.my.walletapp.dto.token.RefreshTokenRequest;
import org.my.walletapp.dto.token.RefreshTokenResponse;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private AuthResponse authResponse;
    private RefreshTokenResponse refreshResponse;

    @BeforeEach
    void setUp() {
        authResponse = new AuthResponse(
                1L,
                "Artem",
                "melnyk.a.yu.-io46@edu.kpi.ua",
                "mockAccessToken",
                "mockRefreshToken"
        );

        refreshResponse = new RefreshTokenResponse(
                "newAccessToken",
                "newRefreshToken"
        );
    }

    @Test
    void login_ShouldReturn200() throws Exception {
        LoginRequest request = new LoginRequest("melnyk.a.yu.-io46@edu.kpi.ua", "Art1634*");

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.email").value("melnyk.a.yu.-io46@edu.kpi.ua"));
    }

    @Test
    void login_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email", "Art1634*");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest("Artem", "melnyk.a.yu.-io46@edu.kpi.ua", "Art1634*");

        when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.email").value("melnyk.a.yu.-io46@edu.kpi.ua"));
    }

    @Test
    void register_ShouldReturn400_WhenPasswordIsWeak() throws Exception {
        RegisterRequest request = new RegisterRequest("Artem", "melnyk.a.yu.-io46@edu.kpi.ua", "123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_ShouldReturn200() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("mockRefreshToken");

        when(authService.refresh(any(RefreshTokenRequest.class))).thenReturn(refreshResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }

    @Test
    void refresh_ShouldReturn400_WhenTokenIsBlank() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("");

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
