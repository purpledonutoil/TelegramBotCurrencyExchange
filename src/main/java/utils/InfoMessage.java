package utils;

import banking.Bank;
import banking.CurrencyRate;

import java.util.List;
import java.util.Map;

public class InfoMessage {

    public static String getPrettyMessage(Map<Bank, List<CurrencyRate>> bankRates) {
        return """
                Курс в (БАНК): (ВАЛЮТА1/ВАЛЮТА2)
                Покупка: ...
                Продаж: ...""";
    }

}
