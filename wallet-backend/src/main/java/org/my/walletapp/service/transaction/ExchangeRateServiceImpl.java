package org.my.walletapp.service.transaction;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService{

    private final Map<String, BigDecimal> ratesToUah = Map.of(
            "UAH", BigDecimal.ONE,
            "USD", new BigDecimal("44.22"),
            "EUR", new BigDecimal("51.30")
    );

    @Override
    public BigDecimal getRate(String fromCurrency, String toCurrency) {
        String from = fromCurrency.toUpperCase();
        String to = toCurrency.toUpperCase();

        if (from.equals(to)) {
            return BigDecimal.ONE;
        }

        if (!ratesToUah.containsKey(from) || !ratesToUah.containsKey(to)) {
            throw new IllegalArgumentException("Unsupported currency pair: " + fromCurrency + " to " + toCurrency);
        }

        BigDecimal fromRate = ratesToUah.get(from);
        BigDecimal toRate = ratesToUah.get(to);

        return fromRate.divide(toRate, 4, RoundingMode.HALF_UP);
    }
}
