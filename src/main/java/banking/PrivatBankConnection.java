package banking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PrivatBankConnection implements BankConnection {
    private static final String API_URL = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=5";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public PrivatBankConnection() {
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
                String currencyA = rate.get("ccy").asText();

                if (currencies.contains(Currency.valueOf(currencyA))) {
                    float buyRate = Float.parseFloat(rate.get("buy").asText());
                    float saleRate = Float.parseFloat(rate.get("sale").asText());
                    ratesList.add(new CurrencyRate(Currency.valueOf(currencyA), buyRate, saleRate));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratesList;
    }
}
