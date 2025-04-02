package org.telegrambot.banking;

public interface CurrencyExchange {
    float getRateBuy(String currencyA, String currencyB);
    float getRateSell(String currencyA, String currencyB);
}
