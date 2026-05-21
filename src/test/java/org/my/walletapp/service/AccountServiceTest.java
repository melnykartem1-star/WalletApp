package org.my.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.dto.account.AccountRequest;
import org.my.walletapp.dto.account.AccountResponse;
import org.my.walletapp.entity.Account;
import org.my.walletapp.entity.User;
import org.my.walletapp.enums.AccountType;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.AccountMapper;
import org.my.walletapp.repository.AccountRepository;
import org.my.walletapp.repository.UserRepository;
import org.my.walletapp.service.account.AccountServiceImpl;
import org.my.walletapp.service.transaction.ExchangeRateService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private UserRepository userRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User testUser;
    private Account testAccount;
    private final Long userId = 1L;
    private final Long accountId = 10L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        testAccount = new Account();
        testAccount.setId(accountId);
        testAccount.setTitle("Main Card");
        testAccount.setBalance(BigDecimal.valueOf(1000));
        testAccount.setCurrency("UAH");
        testAccount.setType(AccountType.CARD);
        testAccount.setActive(true);
        testAccount.setUser(testUser);
    }

    @Nested
    class GetAllAccountsTests {

        @Test
        void getAllAccounts_Success() {
            AccountResponse mockResponse = new AccountResponse(accountId, "Main Card", BigDecimal.valueOf(1000), null, "UAH", true, AccountType.CARD, LocalDateTime.now());

            when(accountRepository.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(testAccount));
            when(accountMapper.toResponse(testAccount)).thenReturn(mockResponse);

            List<AccountResponse> result = accountService.getAllAccounts(userId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Main Card", result.getFirst().title());
            verify(accountRepository, times(1)).findAllByUserIdAndIsActiveTrue(userId);
        }
    }

    @Nested
    class CreateAccountTests {

        @Test
        void createAccount_Success() {
            AccountRequest request = new AccountRequest("New Card", "Desc", "USD", AccountType.CARD);

            Account mappedAccount = new Account();
            mappedAccount.setTitle("New Card");
            AccountResponse mockResponse = new AccountResponse(11L, "New Card", BigDecimal.ZERO, "Desc", "USD", true, AccountType.CARD, LocalDateTime.now());

            when(accountMapper.toEntity(request)).thenReturn(mappedAccount);
            when(userRepository.getReferenceById(userId)).thenReturn(testUser);
            when(accountRepository.save(mappedAccount)).thenReturn(mappedAccount);
            when(accountMapper.toResponse(mappedAccount)).thenReturn(mockResponse);

            AccountResponse result = accountService.createAccount(userId, request);

            assertNotNull(result);
            assertEquals("New Card", result.title());
            assertEquals(testUser, mappedAccount.getUser());
            verify(accountRepository, times(1)).save(mappedAccount);
        }
    }

    @Nested
    class UpdateAccountTests {

        @Test
        void updateAccountById_Success() {
            AccountRequest request = new AccountRequest("Updated Card", null, null, null);

            AccountResponse mockResponse = new AccountResponse(accountId, "Updated Card", BigDecimal.valueOf(1000), null, "UAH", true, AccountType.CARD, LocalDateTime.now());

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)).thenReturn(Optional.of(testAccount));
            when(accountMapper.toResponse(testAccount)).thenReturn(mockResponse);

            AccountResponse result = accountService.updateAccountById(userId, accountId, request);

            assertNotNull(result);
            assertEquals("Updated Card", result.title());
            verify(accountMapper, times(1)).partialUpdate(request, testAccount);
        }

        @Test
        void updateAccountById_ThrowsResourceNotFoundException() {
            AccountRequest request = new AccountRequest("Updated Card", null, null, null);

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccountById(userId, accountId, request));
            verify(accountMapper, never()).partialUpdate(any(), any());
        }
    }

    @Nested
    class GetAccountTests {

        @Test
        void getAccountById_Success() {
            AccountResponse mockResponse = new AccountResponse(accountId, "Main Card", BigDecimal.valueOf(1000), null, "UAH", true, AccountType.CARD, LocalDateTime.now());

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)).thenReturn(Optional.of(testAccount));
            when(accountMapper.toResponse(testAccount)).thenReturn(mockResponse);

            AccountResponse result = accountService.getAccountById(userId, accountId);

            assertNotNull(result);
            assertEquals("Main Card", result.title());
        }

        @Test
        void getAccountById_ThrowsResourceNotFoundException() {
            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(userId, accountId));
        }
    }

    @Nested
    class DeleteAccountTests {

        @Test
        void deleteAccountById_Success() {
            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)).thenReturn(Optional.of(testAccount));

            accountService.deleteAccountById(userId, accountId);

            assertFalse(testAccount.isActive());
        }

        @Test
        void deleteAccountById_ThrowsResourceNotFoundException() {
            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> accountService.deleteAccountById(userId, accountId));
        }
    }
}