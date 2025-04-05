package banking;

public class CurrencyRate {
    private Currency currency;
    private Currency baseCurrency;
    private float buyRate;
    private float saleRate;

    CurrencyRate(){
        this.baseCurrency = Currency.UAH;
    }
}
