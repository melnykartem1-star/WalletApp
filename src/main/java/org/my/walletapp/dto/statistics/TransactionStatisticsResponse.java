package org.my.walletapp.dto.statistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TransactionStatisticsResponse(
        List<CategoryStatResponse> categories,
        BigDecimal balance,
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        LocalDate startDate,
        LocalDate endDate
) {}
