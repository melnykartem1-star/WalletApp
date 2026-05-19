package org.my.walletapp.dto.transaction;

import org.my.walletapp.dto.category.CategoryResponse;
import org.my.walletapp.dto.merchant.MerchantResponse;
import org.my.walletapp.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long targetAccountId,
        Long accountId,
        CategoryResponse category,
        MerchantResponse merchant,
        String title,
        BigDecimal amount,
        String description,
        TransactionType type,
        LocalDateTime createdAt
) {}
