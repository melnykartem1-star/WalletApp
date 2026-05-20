package org.my.walletapp.service.transaction;

import org.my.walletapp.dto.statistics.TransactionStatisticsResponse;
import org.my.walletapp.dto.transaction.TransactionRequest;
import org.my.walletapp.dto.transaction.TransactionResponse;
import org.my.walletapp.dto.transaction.TransferRequest;
import org.my.walletapp.dto.transaction.TransferResponse;
import org.my.walletapp.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TransactionService {

    Page<TransactionResponse> getAllTransactions(
            Pageable pageable,
            TransactionType type,
            Long userId,
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    TransactionStatisticsResponse getTransactionStatistics(
            Long userId,
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    TransferResponse createTransfer(Long userId, TransferRequest request);
    TransactionResponse createTransaction(Long userId, TransactionRequest request);
    TransactionResponse getTransactionById(Long userId, Long transactionId);
    void deleteTransactionById(Long userId, Long transactionId);

}
