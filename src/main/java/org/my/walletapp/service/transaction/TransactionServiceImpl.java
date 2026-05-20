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
import org.my.walletapp.enums.CategoryType;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final MerchantRepository merchantRepository;

    private final TransactionMapper transactionMapper;
    private final TransferMapper transferMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(
            Pageable pageable,
            TransactionType type,
            Long userId,
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {

        Specification<Transaction> spec = Specification.where(TransactionSpecification.byUserId(userId))
                .and(TransactionSpecification.byType(type))
                .and(TransactionSpecification.byCategoryId(categoryId))
                .and(TransactionSpecification.fromDate(startDate))
                .and(TransactionSpecification.toDate(endDate));

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
        List<CategoryStatResponse> categoryStats = new ArrayList<>();

        for (TransactionStatisticProjection proj : projections) {

            if (proj.getType() == CategoryType.EXPENSE) {
                totalExpenses = totalExpenses.add(proj.getAmount());
            }

            else if (proj.getType() == CategoryType.INCOME) {
                totalIncome = totalIncome.add(proj.getAmount());
            }

            categoryStats.add(new CategoryStatResponse(
                    proj.getCategoryName(),
                    proj.getColor(),
                    proj.getAmount()
            ));
        }

        BigDecimal totalBalance = accountRepository.findAllByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionStatisticsResponse(
                categoryStats,
                totalBalance,
                totalIncome,
                totalExpenses,
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

        transaction.setAccount(account);
        transaction.setTargetAccount(targetAccount);
        transaction.setType(TransactionType.TRANSFER);

        account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));

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
