package utils;

import banking.Bank;
import banking.CurrencyRate;

import java.util.List;
import java.util.Map;

public class InfoMessage {
    public static final String RATE_MESSAGE_TEMPLATE = """
                    Курс в %s: %s/%s
                    Купівля: %s
                    Продаж: %s""";
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

            if (currencyRates == null || currencyRates.isEmpty()) {
                result.append(isFirstRecord ? "" : RATE_MESSAGE_DELIMITER);
                result.append("Не вдалось завантажити курси валют для ");
                result.append(bankTitle);

                if (isFirstRecord) {
                    isFirstRecord = false;
                }
                continue;
            }

            for (CurrencyRate currencyRate : currencyRates) {
                String rateResult = formatMessage(currencyRate, bankTitle, roundFormat);

                result.append(isFirstRecord ? "" : RATE_MESSAGE_DELIMITER);
                result.append(rateResult);

                if (isFirstRecord) {
                    isFirstRecord = false;
                }
            }
        }

        return result.toString();
    }

    private static String formatMessage(CurrencyRate rate, String bankTitle, String roundFormat) {
            String buy = rate.getBuyRate() == -1 ? EMPTY_RATE_TEMPLATE : String.format(roundFormat, rate.getBuyRate());
            String sell = rate.getSellRate() == -1 ? EMPTY_RATE_TEMPLATE : String.format(roundFormat, rate.getSellRate());

            return String.format(RATE_MESSAGE_TEMPLATE,
                    bankTitle,
                    rate.getCurrency(),
                    rate.getBaseCurrency(),
                    buy,
                    sell);
    }
}
