package org.my.walletapp.dto.statistics;

import java.math.BigDecimal;

public record CategoryStatResponse(
        String name,
        String color,
        BigDecimal amount
) {}
