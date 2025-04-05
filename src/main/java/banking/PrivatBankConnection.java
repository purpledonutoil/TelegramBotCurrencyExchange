package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PrivatBankConnection implements BankConnection {

    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public List<CurrencyRate> getRates(EnumSet<Currency> currencies) {
        List<CurrencyRate> result = new ArrayList<>();

        int retries = 2;
        while (retries > 0) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return null;
                }

                JsonNode root = mapper.readTree(response.body());

                for (JsonNode rateNode : root) {
                    String currencyA = rateNode.get("ccy").asText();
                    String currencyB = rateNode.get("base_ccy").asText();

                    try {
                        Currency currency = Currency.valueOf(currencyA);
                        Currency baseCurrency = Currency.valueOf(currencyB);

                        if (currencies.contains(currency)) {
                            float buy = rateNode.has("buy") ? (float) rateNode.get("buy").asDouble() : -1;
                            float sell = rateNode.has("sale") ? (float) rateNode.get("sale").asDouble() : -1;

                            CurrencyRate currencyRate = new CurrencyRate(currency, buy, sell);
                            result.add(currencyRate);
                        }
                    } catch (IllegalArgumentException e) {

                    }
                }
                break;
            } catch (IOException | InterruptedException e) {
                retries--;
                if (retries > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        if (retries == 0) {
            return null;
        }

        return result;
    }
}
