package org.my.walletapp.util;

import org.my.walletapp.enums.TransactionType;

import java.math.BigDecimal;

public interface TransactionStatisticProjection {

    String getCategoryName();
    String getColor();
    String getCurrency();
    BigDecimal getAmount();
    TransactionType getType();

}
