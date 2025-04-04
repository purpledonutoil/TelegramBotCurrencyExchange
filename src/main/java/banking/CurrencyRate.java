package banking;

public class CurrencyRate {
    private Currency currency;
    private float buyRate;
    private float saleRate;

    public CurrencyRate(Currency currency, float buyRate, float saleRate) {
        this.currency = currency;
        this.buyRate = buyRate;
        this.saleRate = saleRate;
    }


    public Currency getCurrency() {
        return currency;
    }

    public float getBuyRate() {
        return buyRate;
    }

    public float getSaleRate() {
        return saleRate;
    }

    @Override
    public String toString() {
        return currency + ": " + buyRate + " (buy), " + saleRate + " (sell)";
    }
}
