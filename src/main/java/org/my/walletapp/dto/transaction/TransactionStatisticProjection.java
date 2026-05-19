package org.my.walletapp.dto.transaction;

import org.my.walletapp.enums.CategoryType;

import java.math.BigDecimal;

public interface TransactionStatisticProjection {

    String getCategoryName();
    BigDecimal getAmount();
    CategoryType getType();

}
