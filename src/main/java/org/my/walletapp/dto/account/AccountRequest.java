package org.my.walletapp.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.my.walletapp.enums.AccountType;

public record AccountRequest(

        @NotBlank(message = "Title cannot be empty")
        @Size(max = 255, message = "Title is too long")
        String title,

        @Size(max = 10_000, message = "Description is too long")
        String description,

        @Pattern(
                regexp = "^[A-Z]{3}$",
                message = "Currency must be a valid 3-letter ISO code (e.g., UAH, USD)")
        String currency,

        @NotNull
        AccountType type
) {}
