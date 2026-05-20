package org.my.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.my.walletapp.security.SecurityConfig;
import org.my.walletapp.dto.account.AccountRequest;
import org.my.walletapp.dto.account.AccountResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.enums.AccountType;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private User testUser;
    private AccountResponse testResponse;
    private final Long userId = 1L;
    private final Long accountId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testResponse = new AccountResponse(
                accountId, "Main Card", BigDecimal.valueOf(1000),
                "My salary card", "UAH", true, AccountType.CARD, LocalDateTime.now()
        );
    }

    @Test
    void getAllAccounts_ShouldReturn200AndList() throws Exception {
        when(accountService.getAllAccounts(userId)).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/accounts")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value("Main Card"))
                .andExpect(jsonPath("$[0].balance").value(1000));
    }

    @Test
    void getAccountById_ShouldReturn200AndAccount() throws Exception {
        when(accountService.getAccountById(userId, accountId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId)
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.currency").value("UAH"));
    }

    @Test
    void createAccount_ShouldReturn201_WhenRequestIsValid() throws Exception {
        AccountRequest request = new AccountRequest("Main Card", "My salary card", "UAH", AccountType.CARD);

        when(accountService.createAccount(eq(userId), any(AccountRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/accounts")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Main Card"));
    }

    @Test
    void createAccount_ShouldReturn400_WhenTitleIsBlank() throws Exception {
        AccountRequest request = new AccountRequest("", "My salary card", "UAH", AccountType.CARD);

        mockMvc.perform(post("/api/v1/accounts")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void updateAccountById_ShouldReturn200() throws Exception {
        AccountRequest request = new AccountRequest("Updated Title", null, null, AccountType.CARD);
        AccountResponse updatedResponse = new AccountResponse(accountId, "Updated Title", BigDecimal.valueOf(1000), "My salary card", "UAH", true, AccountType.CARD, LocalDateTime.now());

        when(accountService.updateAccountById(eq(userId), eq(accountId), any(AccountRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/accounts/{id}", accountId)
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteAccountById_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/accounts/{id}", accountId)
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}