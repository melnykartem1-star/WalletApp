package org.my.walletapp.dto.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

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
) {
        @JsonIgnore
        @AssertTrue(message = "Source and target accounts cannot be the same")
        public boolean isDifferentAccounts() {
                if (accountId == null || targetAccountId == null) return true;
                return !accountId.equals(targetAccountId);
        }
}
