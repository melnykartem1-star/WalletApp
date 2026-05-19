package org.my.walletapp.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.my.walletapp.enums.TransactionType;

import java.math.BigDecimal;

public record TransactionRequest(

        @NotNull(message = "Account id cannot be null")
        Long accountId,

        Long categoryId,

        Long merchantId,

        @NotBlank(message = "Title cannot be empty")
        @Size(max = 255, message = "Title is too long")
        String title,

        @NotNull(message = "Transaction type cannot be null")
        TransactionType type,

        @Positive(message = "Amount must be strictly positive")
        @NotNull(message = "Amount cannot be empty")
        BigDecimal amount,

        @Size(max = 10_000, message = "Description is too long")
        String description

) {}
