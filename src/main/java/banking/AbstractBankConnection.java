package banking;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBankConnection {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public HttpResponse<String> connectWithBank(String API_URL) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != HttpStatus.SC_OK) {
            return null;
        }
        return response;
    }

    public int waitAndRetryAgain(int retries){
        retries--;
        if (retries > 0) {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
        }
        return retries;
    }

    public CurrencyRate mapCurrencyRate(JsonNode rateNode, Currency currency, String rateBuy, String rateSell) {
        float buy = rateNode.has(rateBuy) ? (float) rateNode.get(rateBuy).asDouble() : -1;
        float sell = rateNode.has(rateSell) ? (float) rateNode.get(rateSell).asDouble() : -1;
        return new CurrencyRate(currency, buy, sell);
    }

}
