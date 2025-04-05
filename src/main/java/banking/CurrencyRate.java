package banking;

public class CurrencyRate {
    private Currency currency;
    private Currency baseCurrency;
    private float buyRate;
    private float saleRate;
    CurrencyRate(Currency currency, float buyRate, float saleRate) {
        this.currency = currency;
        this.baseCurrency = Currency.UAH;
        this.buyRate = buyRate;
        this.saleRate = saleRate;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setBuyRate(float buyRate) {
        this.buyRate = buyRate;
    }

    public void setSaleRate(float saleRate) {
        this.saleRate = saleRate;
    }

}
