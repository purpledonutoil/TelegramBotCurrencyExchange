package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import executor.TelegramService;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class NBUConnection implements BankConnection {

    private static final String API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<CurrencyRate> getRates(EnumSet<Currency> currencies) {
        List<CurrencyRate> rates = new ArrayList<>();

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

                JsonNode root = objectMapper.readTree(response.body());

                for (JsonNode rateNode : root) {
                    String currencyCode = rateNode.get("cc").asText();

                    try {
                        Currency currency = Currency.valueOf(currencyCode);

                        if (currencies.contains(currency)) {
                            float rate = (float) rateNode.get("rate").asDouble();

                            rates.add(new CurrencyRate(currency, Currency.UAH, rate, rate));
                        } else {
                            rates.add(new CurrencyRate(currency, Currency.UAH, -1, -1));
                        }

                    } catch (IllegalArgumentException ignored) {

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

        return rates;
    }
}
