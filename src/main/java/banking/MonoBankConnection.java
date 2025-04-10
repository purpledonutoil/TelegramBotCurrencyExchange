package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MonoBankConnection implements BankConnection {

    private static final String API_URL = "https://api.monobank.ua/bank/currency";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private int retries = 2;

    private static final Map<Integer, Currency> codeToCurrency = Map.of(
            980, Currency.UAH,
            840, Currency.USD,
            978, Currency.EUR
    );

    @Override
    public List<CurrencyRate> getRates(EnumSet<Currency> currencies) {
        List<CurrencyRate> rates = new ArrayList<>();

        while (retries > 0) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_URL))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != HttpStatus.SC_OK){
                    return null;
                }

                JsonNode root = objectMapper.readTree(response.body());

                for (JsonNode rateNode : root) {
                    int codeA = rateNode.get("currencyCodeA").asInt();
                    int codeB = rateNode.get("currencyCodeB").asInt();

                    if (codeB != 980) continue;

                    Currency currency = codeToCurrency.get(codeA);

                    if (currency != null && currencies.contains(currency)) {
                        float buy = rateNode.has("rateBuy") ? (float) rateNode.get("rateBuy").asDouble() : -1;
                        float sell = rateNode.has("rateSell") ? (float) rateNode.get("rateSell").asDouble() : -1;

                        rates.add(new CurrencyRate(currency, buy, sell));
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
