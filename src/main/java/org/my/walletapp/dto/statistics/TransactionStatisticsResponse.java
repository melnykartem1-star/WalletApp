package org.my.walletapp.dto.statistics;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransactionStatisticsResponse(
        List<CategoryStatResponse> categories,
        BigDecimal balance,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
