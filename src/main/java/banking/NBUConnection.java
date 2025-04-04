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

public class NBUConnection implements BankConnection {
    private static final String API_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private static final Map<String, Currency> currencyMap = Map.of(
            "USD", Currency.USD,
            "EUR", Currency.EUR,
            "UAH", Currency.UAH
    );

    public NBUConnection() {
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

            for (JsonNode rateNode : ratesArray) {
                String currencyCode = rateNode.get("cc").asText();
                float rate = (float) rateNode.get("rate").asDouble();

                if (currencyMap.containsKey(currencyCode) && currencies.contains(currencyMap.get(currencyCode))) {
                    ratesList.add(new CurrencyRate(currencyMap.get(currencyCode), rate, rate));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratesList;
    }
}
