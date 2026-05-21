package org.my.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.my.walletapp.security.SecurityConfig;
import org.my.walletapp.dto.user.PasswordRequest;
import org.my.walletapp.dto.user.UserProfilePatchRequest;
import org.my.walletapp.dto.user.UserProfileResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private User testUser;
    private UserProfileResponse testResponse;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testResponse = new UserProfileResponse(
                userId, "Artem", "melnyk.a.yu.-io46@edu.kpi.ua",
                "uk-UA", "UTC", LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    void getUserProfile_ShouldReturn200() throws Exception {
        when(userService.getUserById(userId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/users/profile")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("melnyk.a.yu.-io46@edu.kpi.ua"));
    }

    @Test
    void updateUserProfile_ShouldReturn200() throws Exception {
        UserProfilePatchRequest request = new UserProfilePatchRequest("New Name", null, null, null);
        UserProfileResponse updatedResponse = new UserProfileResponse(userId, "New Name", "melnyk.a.yu.-io46@edu.kpi.ua", "uk-UA", "UTC", null, null);

        when(userService.updateUserProfile(eq(userId), any(UserProfilePatchRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/users/profile")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void changePassword_ShouldReturn204() throws Exception {
        PasswordRequest request = new PasswordRequest("Art1634*", "NewPassword123!");

        mockMvc.perform(put("/api/v1/users/password")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/users")
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}