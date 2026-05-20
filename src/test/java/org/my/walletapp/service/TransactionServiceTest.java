package org.my.walletapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.my.walletapp.dto.statistics.TransactionStatisticsResponse;
import org.my.walletapp.dto.transaction.TransactionRequest;
import org.my.walletapp.dto.transaction.TransactionResponse;
import org.my.walletapp.dto.transaction.TransferRequest;
import org.my.walletapp.dto.transaction.TransferResponse;
import org.my.walletapp.entity.Account;
import org.my.walletapp.entity.Transaction;
import org.my.walletapp.entity.User;
import org.my.walletapp.enums.CategoryType;
import org.my.walletapp.enums.TransactionType;
import org.my.walletapp.exception.InsufficientFundsException;
import org.my.walletapp.mapper.TransactionMapper;
import org.my.walletapp.mapper.TransferMapper;
import org.my.walletapp.repository.AccountRepository;
import org.my.walletapp.repository.CategoryRepository;
import org.my.walletapp.repository.MerchantRepository;
import org.my.walletapp.repository.TransactionRepository;
import org.my.walletapp.service.transaction.TransactionServiceImpl;
import org.my.walletapp.util.TransactionStatisticProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountRepository accountRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private MerchantRepository merchantRepository;
    @Mock private TransactionMapper transactionMapper;
    @Mock private TransferMapper transferMapper;

    @InjectMocks private TransactionServiceImpl transactionService;

    private User testUser;
    private Account sourceAccount;
    private Account targetAccount;
    private Transaction testTransaction;
    private final Long userId = 1L;
    private final Long transactionId = 100L;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(userId);
        testUser.setName("Artem");
        testUser.setEmail("melnyk.a.yu.-io46@edu.kpi.ua");
        testUser.setPassword("Art1634*");

        sourceAccount = new Account();
        sourceAccount.setId(10L);
        sourceAccount.setBalance(BigDecimal.valueOf(1000));
        sourceAccount.setActive(true);
        sourceAccount.setUser(testUser);

        targetAccount = new Account();
        targetAccount.setId(20L);
        targetAccount.setBalance(BigDecimal.valueOf(500));
        targetAccount.setActive(true);
        targetAccount.setUser(testUser);

        testTransaction = new Transaction();
        testTransaction.setId(transactionId);
        testTransaction.setAccount(sourceAccount);
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setType(TransactionType.WITHDRAW);
    }

    @Nested
    class GetAllAndStatisticsTests {

        @Test
        void getTransactionStatistics_ShouldIncludeIncome() {
            TransactionStatisticProjection incomeProj = mock(TransactionStatisticProjection.class);
            when(incomeProj.getType()).thenReturn(CategoryType.INCOME);
            when(incomeProj.getAmount()).thenReturn(BigDecimal.valueOf(500));
            when(incomeProj.getCategoryName()).thenReturn("Salary");

            when(transactionRepository.getStatisticsByPeriod(any(), eq(userId), any(), any())).thenReturn(List.of(incomeProj));
            when(accountRepository.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(sourceAccount));

            TransactionStatisticsResponse result = transactionService.getTransactionStatistics(userId, null, null, null);

            assertEquals(BigDecimal.valueOf(500), result.totalIncome());
        }

        @Test
        void getAllTransactions_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Transaction> transactionPage = new PageImpl<>(List.of(testTransaction));
            TransactionResponse mockResponse = new TransactionResponse(transactionId, null, 10L, null, null, "Test", BigDecimal.valueOf(100), null, TransactionType.WITHDRAW, LocalDateTime.now());

            when(transactionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(transactionPage);
            when(transactionMapper.toResponse(testTransaction)).thenReturn(mockResponse);

            Page<TransactionResponse> result = transactionService.getAllTransactions(pageable, null, userId, null, null, null);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }

        @Test
        void getTransactionStatistics_Success() {
            TransactionStatisticProjection proj = mock(TransactionStatisticProjection.class);
            when(proj.getCategoryName()).thenReturn("Food");
            when(proj.getAmount()).thenReturn(BigDecimal.valueOf(200));
            when(proj.getType()).thenReturn(CategoryType.EXPENSE);

            when(transactionRepository.getStatisticsByPeriod(null, userId, null, null)).thenReturn(List.of(proj));
            when(accountRepository.findAllByUserIdAndIsActiveTrue(userId)).thenReturn(List.of(sourceAccount, targetAccount));

            TransactionStatisticsResponse result = transactionService.getTransactionStatistics(userId, null, null, null);

            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(1500), result.balance());
            assertEquals(BigDecimal.valueOf(200), result.totalExpenses());
            assertEquals(1, result.categories().size());
        }

        @Test
        void getAllTransactions_WithPaginationAndFilters_Success() {
            int pageNumber = 1;
            int pageSize = 5;
            Pageable pageable = PageRequest.of(pageNumber, pageSize);

            TransactionType filterType = TransactionType.WITHDRAW;
            Long filterCategoryId = 2L;
            LocalDateTime startDate = LocalDateTime.now().minusDays(7);
            LocalDateTime endDate = LocalDateTime.now();

            Page<Transaction> mockPage = new PageImpl<>(List.of(testTransaction), pageable, 1);
            TransactionResponse mockResponse = new TransactionResponse(100L, null, 10L, null, null, "Test", BigDecimal.valueOf(100), null, TransactionType.WITHDRAW, LocalDateTime.now());

            when(transactionRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(mockPage);
            when(transactionMapper.toResponse(testTransaction)).thenReturn(mockResponse);

            Page<TransactionResponse> result = transactionService.getAllTransactions(
                    pageable, filterType, userId, filterCategoryId, startDate, endDate
            );

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals(pageNumber, result.getPageable().getPageNumber());
            assertEquals(pageSize, result.getPageable().getPageSize());

            verify(transactionRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    class CreateTransferTests {

        @Test
        void createTransfer_Success() {
            TransferRequest request = new TransferRequest(10L, 20L, "Transfer", BigDecimal.valueOf(300), null);
            Transaction mappedTx = new Transaction();
            mappedTx.setAmount(BigDecimal.valueOf(300));
            TransferResponse mockResponse = new TransferResponse(transactionId, 10L, 20L, null, "Transfer", BigDecimal.valueOf(300), null, TransactionType.TRANSFER, LocalDateTime.now());

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(10L, userId)).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(20L, userId)).thenReturn(Optional.of(targetAccount));
            when(transferMapper.toEntity(request)).thenReturn(mappedTx);
            when(transferMapper.toResponse(mappedTx)).thenReturn(mockResponse);

            TransferResponse result = transactionService.createTransfer(userId, request);

            assertNotNull(result);
            assertEquals(BigDecimal.valueOf(700), sourceAccount.getBalance());
            assertEquals(BigDecimal.valueOf(800), targetAccount.getBalance());
            verify(transactionRepository, times(1)).save(mappedTx);
        }

        @Test
        void createTransaction_WithCategoryAndMerchant_Success() {
            TransactionRequest request = new TransactionRequest(10L, 2L, 3L, "Buy", TransactionType.WITHDRAW, BigDecimal.valueOf(200), null);
            Transaction mappedTx = new Transaction();
            mappedTx.setAmount(BigDecimal.valueOf(200));
            mappedTx.setType(TransactionType.WITHDRAW);

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(10L, userId)).thenReturn(Optional.of(sourceAccount));
            when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(2L, userId)).thenReturn(Optional.of(new org.my.walletapp.entity.Category()));
            when(merchantRepository.findByIdAndUserIdAndIsActiveTrue(3L, userId)).thenReturn(Optional.of(new org.my.walletapp.entity.Merchant()));

            when(transactionMapper.toEntity(request)).thenReturn(mappedTx);
            when(transactionMapper.toResponse(mappedTx)).thenReturn(mock(TransactionResponse.class));

            transactionService.createTransaction(userId, request);

            verify(categoryRepository).findByIdAndUserIdAndIsActiveTrue(2L, userId);
            verify(merchantRepository).findByIdAndUserIdAndIsActiveTrue(3L, userId);
            verify(transactionRepository).save(mappedTx);
        }

        @Test
        void createTransfer_ThrowsInsufficientFundsException() {
            TransferRequest request = new TransferRequest(10L, 20L, "Transfer", BigDecimal.valueOf(1500), null);
            Transaction mappedTx = new Transaction();
            mappedTx.setAmount(BigDecimal.valueOf(1500));

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(10L, userId)).thenReturn(Optional.of(sourceAccount));
            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(20L, userId)).thenReturn(Optional.of(targetAccount));
            when(transferMapper.toEntity(request)).thenReturn(mappedTx);

            assertThrows(InsufficientFundsException.class, () -> transactionService.createTransfer(userId, request));
            verify(transactionRepository, never()).save(any());
        }
    }

    @Nested
    class GetTransactionTests {
        @Test
        void getTransactionById_Success() {
            when(transactionRepository.findByIdAndAccount_UserId(transactionId, userId))
                    .thenReturn(Optional.of(testTransaction));
            when(transactionMapper.toResponse(testTransaction))
                    .thenReturn(mock(TransactionResponse.class));

            assertDoesNotThrow(() -> transactionService.getTransactionById(userId, transactionId));
            verify(transactionRepository).findByIdAndAccount_UserId(transactionId, userId);
        }
    }

    @Nested
    class CreateTransactionTests {

        @Test
        void createTransaction_Withdraw_Success() {
            TransactionRequest request = new TransactionRequest(10L, null, null, "Buy", TransactionType.WITHDRAW, BigDecimal.valueOf(200), null);
            Transaction mappedTx = new Transaction();
            mappedTx.setAmount(BigDecimal.valueOf(200));
            mappedTx.setType(TransactionType.WITHDRAW);

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(10L, userId)).thenReturn(Optional.of(sourceAccount));
            when(transactionMapper.toEntity(request)).thenReturn(mappedTx);
            when(transactionMapper.toResponse(mappedTx)).thenReturn(mock(TransactionResponse.class));

            transactionService.createTransaction(userId, request);

            assertEquals(BigDecimal.valueOf(800), sourceAccount.getBalance());
            verify(transactionRepository, times(1)).save(mappedTx);
        }

        @Test
        void createTransaction_Withdraw_ThrowsInsufficientFundsException() {
            TransactionRequest request = new TransactionRequest(10L, null, null, "Buy", TransactionType.WITHDRAW, BigDecimal.valueOf(2000), null);
            Transaction mappedTx = new Transaction();
            mappedTx.setAmount(BigDecimal.valueOf(2000));
            mappedTx.setType(TransactionType.WITHDRAW);

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(10L, userId)).thenReturn(Optional.of(sourceAccount));
            when(transactionMapper.toEntity(request)).thenReturn(mappedTx);

            assertThrows(InsufficientFundsException.class, () -> transactionService.createTransaction(userId, request));
        }

        @Test
        void createTransaction_Deposit_Success() {
            TransactionRequest request = new TransactionRequest(10L, null, null, "Salary", TransactionType.DEPOSIT, BigDecimal.valueOf(500), null);
            Transaction mappedTx = new Transaction();
            mappedTx.setAmount(BigDecimal.valueOf(500));
            mappedTx.setType(TransactionType.DEPOSIT);

            when(accountRepository.findByIdAndUserIdAndIsActiveTrue(10L, userId)).thenReturn(Optional.of(sourceAccount));
            when(transactionMapper.toEntity(request)).thenReturn(mappedTx);
            when(transactionMapper.toResponse(mappedTx)).thenReturn(mock(TransactionResponse.class));

            transactionService.createTransaction(userId, request);

            assertEquals(BigDecimal.valueOf(1500), sourceAccount.getBalance());
            verify(transactionRepository, times(1)).save(mappedTx);
        }
    }

    @Nested
    class DeleteTransactionTests {

        @Test
        void deleteTransactionById_Withdraw_Success() {
            when(transactionRepository.findByIdAndAccount_UserId(transactionId, userId)).thenReturn(Optional.of(testTransaction));

            transactionService.deleteTransactionById(userId, transactionId);

            assertEquals(BigDecimal.valueOf(1100), sourceAccount.getBalance());
            verify(transactionRepository, times(1)).delete(testTransaction);
        }

        @Test
        void deleteTransactionById_Deposit_Success() {
            testTransaction.setType(TransactionType.DEPOSIT);
            when(transactionRepository.findByIdAndAccount_UserId(transactionId, userId)).thenReturn(Optional.of(testTransaction));

            transactionService.deleteTransactionById(userId, transactionId);

            assertEquals(BigDecimal.valueOf(900), sourceAccount.getBalance());
            verify(transactionRepository, times(1)).delete(testTransaction);
        }

        @Test
        void deleteTransactionById_Deposit_ThrowsInsufficientFundsException() {
            testTransaction.setType(TransactionType.DEPOSIT);
            testTransaction.setAmount(BigDecimal.valueOf(2000));
            when(transactionRepository.findByIdAndAccount_UserId(transactionId, userId)).thenReturn(Optional.of(testTransaction));

            assertThrows(InsufficientFundsException.class, () -> transactionService.deleteTransactionById(userId, transactionId));
            verify(transactionRepository, never()).delete(any(Transaction.class));
        }

        @Test
        void deleteTransactionById_Transfer_Success() {
            testTransaction.setType(TransactionType.TRANSFER);
            testTransaction.setTargetAccount(targetAccount);
            when(transactionRepository.findByIdAndAccount_UserId(transactionId, userId)).thenReturn(Optional.of(testTransaction));

            transactionService.deleteTransactionById(userId, transactionId);

            assertEquals(BigDecimal.valueOf(1100), sourceAccount.getBalance());
            assertEquals(BigDecimal.valueOf(400), targetAccount.getBalance());
            verify(transactionRepository, times(1)).delete(testTransaction);
        }
    }
}