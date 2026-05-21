package org.my.walletapp.service.transaction;

import lombok.RequiredArgsConstructor;
import org.my.walletapp.dto.statistics.CategoryStatResponse;
import org.my.walletapp.dto.statistics.TransactionStatisticsResponse;
import org.my.walletapp.dto.transaction.TransactionRequest;
import org.my.walletapp.dto.transaction.TransactionResponse;
import org.my.walletapp.dto.transaction.TransferRequest;
import org.my.walletapp.dto.transaction.TransferResponse;
import org.my.walletapp.entity.Account;
import org.my.walletapp.entity.Category;
import org.my.walletapp.entity.Merchant;
import org.my.walletapp.entity.Transaction;
import org.my.walletapp.enums.TransactionType;
import org.my.walletapp.exception.InsufficientFundsException;
import org.my.walletapp.exception.ResourceNotFoundException;
import org.my.walletapp.mapper.TransactionMapper;
import org.my.walletapp.mapper.TransferMapper;
import org.my.walletapp.repository.AccountRepository;
import org.my.walletapp.repository.CategoryRepository;
import org.my.walletapp.repository.MerchantRepository;
import org.my.walletapp.repository.TransactionRepository;
import org.my.walletapp.util.TransactionSpecification;
import org.my.walletapp.util.TransactionStatisticProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final MerchantRepository merchantRepository;

    private final ExchangeRateService exchangeRateService;

    private final TransactionMapper transactionMapper;
    private final TransferMapper transferMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(
            Pageable pageable, TransactionType type, Long userId, Long categoryId,
            LocalDateTime startDate, LocalDateTime endDate, String query
    ) {
        Specification<Transaction> spec = Specification.where(TransactionSpecification.byUserId(userId))
                .and(TransactionSpecification.byType(type))
                .and(TransactionSpecification.byCategoryId(categoryId))
                .and(TransactionSpecification.fromDate(startDate))
                .and(TransactionSpecification.toDate(endDate))
                .and(TransactionSpecification.byTitle(query));

        return transactionRepository.findAll(spec, pageable)
                .map(transactionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionStatisticsResponse getTransactionStatistics(
            Long userId,
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        List<TransactionStatisticProjection> projections = transactionRepository.getStatisticsByPeriod(
                categoryId, userId, startDate, endDate
        );

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        Map<String, BigDecimal> categoryAmounts = new HashMap<>();
        Map<String, String> categoryColors = new HashMap<>();

        for (TransactionStatisticProjection proj : projections) {
            if (proj == null || proj.getAmount() == null) continue;

            System.out.println("DEBUG: Category: " + proj.getCategoryName() +
                    ", Type: " + proj.getType() +
                    ", Amount: " + proj.getAmount() +
                    ", Currency: " + proj.getCurrency());

            String currency = proj.getCurrency();
            BigDecimal amount = proj.getAmount().abs();

            BigDecimal rate = exchangeRateService.getRate(currency, "UAH");
            BigDecimal amountInUah = amount.multiply(rate);

            if (proj.getType() == TransactionType.WITHDRAW) {
                totalExpenses = totalExpenses.add(amountInUah);

                String categoryName = proj.getCategoryName() != null ? proj.getCategoryName() : "Without category";
                String color = proj.getColor() != null ? proj.getColor() : "#CCCCCC";

                categoryAmounts.put(categoryName, categoryAmounts.getOrDefault(categoryName, BigDecimal.ZERO).add(amountInUah));
                categoryColors.put(categoryName, color);
            }
            else if (proj.getType() == TransactionType.DEPOSIT) {
                totalIncome = totalIncome.add(amountInUah);
            }
        }

        List<CategoryStatResponse> categoryStats = categoryAmounts.entrySet().stream()
                .map(entry -> new CategoryStatResponse(
                        entry.getKey(),
                        categoryColors.get(entry.getKey()),
                        entry.getValue().setScale(2, RoundingMode.HALF_UP)
                ))
                .toList();

        List<Object[]> stats = transactionRepository.getBalanceByCurrency(userId);
        BigDecimal totalBalanceInUah = BigDecimal.ZERO;

        if (stats != null) {
            for (Object[] row : stats) {
                if (row[0] == null || row[1] == null) continue;

                String currency = (String) row[0];
                BigDecimal balance = (BigDecimal) row[1];

                BigDecimal rate = exchangeRateService.getRate(currency, "UAH");
                totalBalanceInUah = totalBalanceInUah.add(balance.multiply(rate));
            }
        }

        return new TransactionStatisticsResponse(
                categoryStats,
                totalBalanceInUah.setScale(2, RoundingMode.HALF_UP),
                totalIncome.setScale(2, RoundingMode.HALF_UP),
                totalExpenses.setScale(2, RoundingMode.HALF_UP),
                startDate,
                endDate
        );
    }

    @Override
    @Transactional
    public TransferResponse createTransfer(Long userId, TransferRequest request) {
        Account account = accountRepository.findByIdAndUserIdAndIsActiveTrue(request.accountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Source account with id " + request.accountId() + " not found"));

        Account targetAccount = accountRepository.findByIdAndUserIdAndIsActiveTrue(request.targetAccountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Target account with id " + request.targetAccountId() + " not found"));

        Transaction transaction = transferMapper.toEntity(request);

        if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds: source account balance cannot be negative.");
        }

        BigDecimal amountToDeduct = transaction.getAmount();
        BigDecimal amountToAdd = amountToDeduct;

        if (!account.getCurrency().equalsIgnoreCase(targetAccount.getCurrency())) {
            BigDecimal exchangeRate = exchangeRateService.getRate(account.getCurrency(), targetAccount.getCurrency());
            amountToAdd = amountToDeduct.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        }

        account.setBalance(account.getBalance().subtract(amountToDeduct));
        targetAccount.setBalance(targetAccount.getBalance().add(amountToAdd));

        transaction.setAccount(account);
        transaction.setTargetAccount(targetAccount);
        transaction.setType(TransactionType.TRANSFER);

        transactionRepository.save(transaction);

        return transferMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        Account account = accountRepository.findByIdAndUserIdAndIsActiveTrue(request.accountId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Account with id " + request.accountId() + " not found"));

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setAccount(account);

        if (request.categoryId() != null) {
            Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(request.categoryId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with id " + request.categoryId() + " not found"));
            transaction.setCategory(category);
        }

        if (request.merchantId() != null) {
            Merchant merchant = merchantRepository.findByIdAndUserIdAndIsActiveTrue(request.merchantId(), userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Merchant with id " + request.merchantId() + " not found"));
            transaction.setMerchant(merchant);
        }

        if (transaction.getType() == TransactionType.WITHDRAW) {
            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientFundsException("Insufficient funds: account balance cannot be negative.");
            }
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else if (transaction.getType() == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
        }

        transactionRepository.save(transaction);
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndAccount_UserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public void deleteTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndAccount_UserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));

        Account account = transaction.getAccount();
        Account targetAccount = transaction.getTargetAccount();

        if (transaction.getType() == TransactionType.WITHDRAW) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));

        } else if (transaction.getType() == TransactionType.DEPOSIT) {
            if (account.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new InsufficientFundsException("Cannot delete deposit: account balance would become negative.");
            }
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));

        } else if (transaction.getType() == TransactionType.TRANSFER) {
            account.setBalance(account.getBalance().add(transaction.getAmount()));
            targetAccount.setBalance(targetAccount.getBalance().subtract(transaction.getAmount()));
        }

        transactionRepository.delete(transaction);
    }
}
