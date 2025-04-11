package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Collections;

public class NBUConnection extends AbstractBankConnection implements BankConnection {

    private static final String API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private final ObjectMapper objectMapper = new ObjectMapper();
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

                JsonNode root = objectMapper.readTree(response.body());

                for (JsonNode rateNode : root) {
                    String currencyCode = rateNode.get("cc").asText();

                    try {
                        Currency currency = Currency.valueOf(currencyCode);

                        if (currencies.contains(currency)) {
                            float rate = (float) rateNode.get("rate").asDouble();
                            rates.add(new CurrencyRate(currency, rate, rate));
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
