package banking;

import java.util.List;

public interface BankConnection {
    List<CurrencyRate> getRates(List<String> currencies);
}
