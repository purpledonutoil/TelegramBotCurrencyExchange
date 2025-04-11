package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MonoBankConnection extends AbstractBankConnection implements BankConnection {

    private static final String API_URL = "https://api.monobank.ua/bank/currency";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_RETRIES = 2;

    private static final Map<Integer, Currency> codeToCurrency = Map.of(
            980, Currency.UAH,
            840, Currency.USD,
            978, Currency.EUR
    );

    @Override
    public List<CurrencyRate> getRates(EnumSet<Currency> currencies) {
        int retries = MAX_RETRIES;
        List<CurrencyRate> rates = new ArrayList<>();

        while (retries > 0) {
            try {
                HttpResponse<String> response = connectWithBank(API_URL);

                if (response==null){

                    return Collections.emptyList();
                }

                JsonNode root = objectMapper.readTree(response.body());

                for (JsonNode rateNode : root) {
                    int codeA = rateNode.get("currencyCodeA").asInt();
                    int codeB = rateNode.get("currencyCodeB").asInt();

                    if (codeB != 980) continue;

                    Currency currency = codeToCurrency.get(codeA);

                    if (currencies.contains(currency)) {
                        rates.add(mapCurrencyRate(rateNode, currency, "rateBuy", "rateSell"));
                        if (rates.size() == currencies.size()) {
                            break;
                        }
                    }
                }
                break;
            } catch (IOException | InterruptedException e) {
                retries=waitAndRetryAgain(retries);
            }
        }

        if (retries == 0) {
            return Collections.emptyList();
        }

        return rates;
    }
}
