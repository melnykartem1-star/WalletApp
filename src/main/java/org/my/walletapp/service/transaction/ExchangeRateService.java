package org.my.walletapp.service.transaction;

import java.math.BigDecimal;

public interface ExchangeRateService {
    BigDecimal getRate(String fromCurrency, String toCurrency);
}
