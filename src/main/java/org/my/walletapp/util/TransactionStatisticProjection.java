package org.my.walletapp.util;

import org.my.walletapp.enums.CategoryType;

import java.math.BigDecimal;

public interface TransactionStatisticProjection {

    String getCategoryName();
    String getColor();
    BigDecimal getAmount();
    CategoryType getType();

}
