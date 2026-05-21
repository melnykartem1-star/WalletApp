package org.my.walletapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.my.walletapp.dto.statistics.TransactionStatisticsResponse;
import org.my.walletapp.dto.transaction.TransactionRequest;
import org.my.walletapp.dto.transaction.TransactionResponse;
import org.my.walletapp.dto.transaction.TransferRequest;
import org.my.walletapp.dto.transaction.TransferResponse;
import org.my.walletapp.entity.User;
import org.my.walletapp.enums.TransactionType;
import org.my.walletapp.security.JwtAuthenticationFilter;
import org.my.walletapp.security.JwtService;
import org.my.walletapp.security.SecurityConfig;
import org.my.walletapp.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private User testUser;
    private TransactionResponse txResponse;
    private final Long userId = 1L;
    private final Long txId = 100L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        txResponse = new TransactionResponse(
                txId, null, 10L, null, null, "Test",
                BigDecimal.valueOf(100), null, TransactionType.WITHDRAW, "UAH", LocalDateTime.now()
        );
    }

    @Test
    void getAllTransactions_ShouldReturn200AndPage() throws Exception {
        when(transactionService.getAllTransactions(any(Pageable.class), any(), eq(userId), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(txResponse)));

        mockMvc.perform(get("/api/v1/transactions")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1));
    }

    @Test
    void createTransaction_ShouldReturn201() throws Exception {
        TransactionRequest request = new TransactionRequest(10L, null, null, "Buy", TransactionType.WITHDRAW, BigDecimal.valueOf(100), null);

        when(transactionService.createTransaction(eq(userId), any(TransactionRequest.class)))
                .thenReturn(txResponse);

        mockMvc.perform(post("/api/v1/transactions")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test"));
    }

    @Test
    void getTransactionById_ShouldReturn200() throws Exception {
        when(transactionService.getTransactionById(userId, txId)).thenReturn(txResponse);
        mockMvc.perform(get("/api/v1/transactions/{id}", txId)
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(txId));
    }

    @Test
    void getTransactionStatistics_ShouldReturn200() throws Exception {
        TransactionStatisticsResponse stats = new TransactionStatisticsResponse(
                List.of(),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(500),
                BigDecimal.valueOf(200),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(transactionService.getTransactionStatistics(eq(userId), any(), any(), any()))
                .thenReturn(stats);

        mockMvc.perform(get("/api/v1/transactions/statistics")
                        .with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.totalExpenses").value(200));
    }

    @Test
    void createTransfer_ShouldReturn201() throws Exception {
        TransferRequest request = new TransferRequest(10L, 20L, "Transfer", BigDecimal.valueOf(50), null);
        TransferResponse transferResponse = new TransferResponse(101L, 10L, 20L, null, "Transfer", BigDecimal.valueOf(50), null, TransactionType.TRANSFER, LocalDateTime.now());

        when(transactionService.createTransfer(eq(userId), any(TransferRequest.class)))
                .thenReturn(transferResponse);

        mockMvc.perform(post("/api/v1/transactions/transfers")
                        .with(user(testUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(50));
    }

    @Test
    void deleteTransactionById_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/transactions/{id}", txId)
                        .with(user(testUser))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
