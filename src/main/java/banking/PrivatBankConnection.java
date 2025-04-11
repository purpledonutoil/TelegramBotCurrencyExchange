package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PrivatBankConnection extends AbstractBankConnection implements BankConnection {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private final ObjectMapper mapper = new ObjectMapper();
    private static final int MAX_RETRIES = 2;

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

                JsonNode root = mapper.readTree(response.body());

                for (JsonNode rateNode : root) {
                    String currencyA = rateNode.get("ccy").asText();

                    try {
                        Currency currency = Currency.valueOf(currencyA);

                        if (currencies.contains(currency)) {
                            rates.add(mapCurrencyRate(rateNode, currency, "buy", "sale"));
                            if (rates.size() == currencies.size()) {
                                break;
                            }
                        }
                    } catch (IllegalArgumentException e) {

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
