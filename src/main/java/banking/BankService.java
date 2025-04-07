package banking;

import storage.UserSettings;

import java.util.*;

public class BankService {
    public static Map<Bank, List<CurrencyRate>> getBankRates(UserSettings settings) {
        Map<Bank, List<CurrencyRate>> result = new EnumMap<>(Bank.class);

        EnumSet<Bank> selectedBanks = settings.getBanks();
        EnumSet<Currency> selectedCurrencies = settings.getCurrencies();

        for (Bank bank : selectedBanks) {
            BankConnection connection = getBankConnection(bank);
            if (connection != null) {
                List<CurrencyRate> rates = connection.getRates(selectedCurrencies);

                result.put(bank, rates);
            } else {
                result.put(bank, null);
            }
        }
        return result;
    }

    private static BankConnection getBankConnection(Bank bank) {
        return switch (bank) {
            case PRIVAT -> new PrivatBankConnection();
            case MONO -> new MonoBankConnection();
            case NBU -> new NBUConnection();
        };
    }
}
