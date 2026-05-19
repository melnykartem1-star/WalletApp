package org.my.walletapp.dto.account;

import org.my.walletapp.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String title,
        BigDecimal balance,
        String description,
        String currency,
        boolean isActive,
        AccountType type,
        LocalDate createdAt
) {}
