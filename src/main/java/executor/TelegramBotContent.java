package executor;

import java.util.LinkedHashMap;
import java.util.Map;

public class TelegramBotContent {
    private TelegramBotContent(){
    }
    protected static final String MESSAGE1 =
            "Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют.";
    protected static final Map<String, String> BUTTONS1 = Map.of(
            "Отримати інфо", "info_btn",
            "Налаштування", "settings_btn");
    protected static final String MESSAGE2 = "Налаштування:";
    protected static final Map<String, String> BUTTONS2 = new LinkedHashMap<>();

    protected static final String MESSAGE3 = "Оберіть кількість знаків після коми:";
    protected static final Map<String, String> BUTTONS3 = new LinkedHashMap<>();
    protected static final String MESSAGE4 = "Оберіть один або декілька банків:";
    protected static final Map<String, String> BUTTONS4 = new LinkedHashMap<>();
    protected static final String MESSAGE5 = "Оберіть одну або декілька валют:";
    protected static final Map<String, String> BUTTONS5 = new LinkedHashMap<>();

    protected static final String MESSAGE6 = "Оберіть час сповіщення:";

    protected static final Map<String, String> BUTTONS6 = new LinkedHashMap<>();

    protected static final String MESSAGE7 = """
            Курс в (БАНК): (ВАЛЮТА1/ВАЛЮТА2)
            Покупка: ...
            Продаж: ...""";

    static {
        BUTTONS2.put("Кількість знаків після коми", "decimalpoint_btn");
        BUTTONS2.put("Банк", "bank_btn");
        BUTTONS2.put("Валюти", "currency_btn");
        BUTTONS2.put("Час оповіщень", "notification_btn");

        BUTTONS3.put("2", "decimalpoint_btn1");
        BUTTONS3.put("3", "decimalpoint_btn2");
        BUTTONS3.put("4", "decimalpoint_btn3");

        BUTTONS4.put("НБУ", "bank_btn1");
        BUTTONS4.put("ПриватБанк", "bank_btn2");
        BUTTONS4.put("Монобанк", "bank_btn3");

        BUTTONS5.put("USD", "currency_btn1");
        BUTTONS5.put("EUR", "currency_btn2");
        BUTTONS5.put("UAH", "currency_btn3");

        BUTTONS6.put("9", "notification_btn1");
        BUTTONS6.put("10", "notification_btn2");
        BUTTONS6.put("11", "notification_btn3");
        BUTTONS6.put("12", "notification_btn4");
        BUTTONS6.put("13", "notification_btn5");
        BUTTONS6.put("14", "notification_btn6");
        BUTTONS6.put("15", "notification_btn7");
        BUTTONS6.put("16", "notification_btn8");
        BUTTONS6.put("17", "notification_btn9");
        BUTTONS6.put("18", "notification_btn10");
        BUTTONS6.put("Вимкнути повідомлення", "notification_btn11");
    }
}
