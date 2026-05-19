package org.my.walletapp.dto.transaction;

import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferResponse(
        Long id,
        Long accountId,
        Long targetAccountId,
        CategoryResponse category,
        String title,
        BigDecimal amount,
        String description,
        TransactionType type,
        LocalDate createdAt

) {}
