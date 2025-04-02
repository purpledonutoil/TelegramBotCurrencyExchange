package org.telegrambot.banking;

import com.google.gson.JsonObject;

public interface BankConnection {
    JsonObject getAPI(String currencyNameA, String currencyNameB);
    float getMonoBankRate(JsonObject apiResponse, String currencyA, String currencyB, String rateType);
    float getPrivatBankRate(JsonObject apiResponse, String currencyA, String currencyB, String rateType);
    int getMonoCurrencyID(String currency);
}
