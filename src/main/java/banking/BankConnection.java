package banking;

import java.util.EnumSet;
import java.util.List;

public interface BankConnection {
    List<CurrencyRate> getRates(EnumSet<Currency> currencies);
}
