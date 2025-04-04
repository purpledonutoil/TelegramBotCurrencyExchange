package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MonoBankConnection implements BankConnection {
    private static final String API_URL = "https://api.monobank.ua/bank/currency";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final Map<Integer, Currency> currencyCodeMap = Map.of(
            840, Currency.USD,
            978, Currency.EUR
    );

    public MonoBankConnection() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<CurrencyRate> getRates(List<String> currencies) {
        List<CurrencyRate> ratesList = new ArrayList<>();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode ratesArray = objectMapper.readTree(response.body());

            for (JsonNode rate : ratesArray) {
                int currencyCodeA = rate.get("currencyCodeA").asInt();

                if (currencyCodeMap.containsKey(currencyCodeA) && currencies.contains(currencyCodeMap.get(currencyCodeA))) {
                    float buyRate = Float.parseFloat(rate.get("rateBuy").asText());
                    float saleRate = Float.parseFloat(rate.get("rateSell").asText());
                    ratesList.add(new CurrencyRate(currencyCodeMap.get(currencyCodeA), buyRate, saleRate));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratesList;
    }
}
