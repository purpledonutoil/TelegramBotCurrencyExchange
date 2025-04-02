package banking;

import com.google.gson.JsonObject;

public interface BankConnection {
    JsonObject getAPI(String currencyNameA, String currencyNameB);
    float getRateBuy(String currencyA, String currencyB);
    float getRateSell(String currencyA, String currencyB);
}
