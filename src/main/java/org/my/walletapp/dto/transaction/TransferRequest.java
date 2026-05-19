package org.my.walletapp.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(

        @NotNull(message = "Account id cannot be null")
        Long accountId,

        @NotNull(message = "Target account id cannot be null")
        Long targetAccountId,

        @NotBlank(message = "Title cannot be empty")
        @Size(max = 255, message = "Title is too long")
        String title,

        @Positive(message = "Amount must be strictly positive")
        @NotNull(message = "Amount cannot be empty")
        BigDecimal amount,

        @Size(max = 10_000, message = "Description is too long")
        String description
) {}
