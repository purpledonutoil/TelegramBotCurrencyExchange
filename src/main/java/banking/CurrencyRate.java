package banking;

public class CurrencyRate {
    private Currency currency;
    private Currency baseCurrency;
    private float buyRate;
    private float sellRate;

    CurrencyRate(Currency currency, float buyRate, float sellRate) {
        this.currency = currency;
        this.baseCurrency = Currency.UAH;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public float getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(float buyRate) {
        this.buyRate = buyRate;
    }

    public float getSellRate() {
        return sellRate;
    }

    public void setSellRate(float sellRate) {
        this.sellRate = sellRate;
    }

    @Override
    public String toString() {
        return String.format("Currency %s/%s | Buy %.6f | Sell %.6f",
                currency, baseCurrency, buyRate, sellRate);

    }
}
