package utils;

import banking.Bank;
import banking.CurrencyRate;

import java.util.List;
import java.util.Map;

public class InfoMessage {
    public static final String RATE_MESSAGE_TEMPLATE = """
            Курс в %bank%: %currency%/%baseCurrency%
            Покупка: %buyRate%
            Продаж: %sellRate%""";
    public static final String RATE_MESSAGE_DELIMITER = "\n-----------------\n";
    public static final String EMPTY_RATE_TEMPLATE = "no data";

    private InfoMessage() {
    }

    public static String getPrettyMessage(Map<Bank, List<CurrencyRate>> bankRates, int roundNumber) {

        if (bankRates == null || bankRates.isEmpty()) {
            return "Не вдалося отримати з'єднання з банками.";
        }

        String roundFormat = "%." + roundNumber + "f";
        StringBuilder result = new StringBuilder();
        boolean isFirstRecord = true;
        for (Map.Entry<Bank, List<CurrencyRate>> bankRatesEntry : bankRates.entrySet()) {
            Bank bank = bankRatesEntry.getKey();
            List<CurrencyRate> currencyRates = bankRatesEntry.getValue();
            String bankTitle = bank.getTitle();

            if (currencyRates.isEmpty()) {
                result.append(isFirstRecord ? "" : RATE_MESSAGE_DELIMITER);
                result.append("Не вдалось завантажити курси валют для ");
                result.append(bankTitle);

                if (isFirstRecord) {
                    isFirstRecord = false;
                }
                continue;
            }

            for (CurrencyRate currencyRate : currencyRates) {
                float buyRate = currencyRate.getBuyRate();
                float sellRate = currencyRate.getSellRate();
                String currentBuyRate = buyRate == -1 ? EMPTY_RATE_TEMPLATE : String.format(roundFormat, buyRate);
                String currentSellRate = sellRate == -1 ? EMPTY_RATE_TEMPLATE : String.format(roundFormat, sellRate);

                String rateResult = RATE_MESSAGE_TEMPLATE.replace("%bank%", bankTitle)
                        .replace("%currency%", currencyRate.getCurrency().toString())
                        .replace("%baseCurrency%", currencyRate.getBaseCurrency().toString())
                        .replace("%buyRate%", currentBuyRate)
                        .replace("%sellRate%", currentSellRate);

                result.append(isFirstRecord ? "" : RATE_MESSAGE_DELIMITER);
                result.append(rateResult);

                if (isFirstRecord) {
                    isFirstRecord = false;
                }
            }
        }

        return result.toString();
    }

}
