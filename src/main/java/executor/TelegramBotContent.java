package executor;

import java.util.LinkedHashMap;
import java.util.Map;

public class TelegramBotContent {
    private TelegramBotContent(){
    }
    protected static final String MESSAGE1 = "Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют.";
    protected static final Map<String, String> BUTTONS1 = new LinkedHashMap<>();

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

    static {
        BUTTONS1.put("info_btn", "Отримати інфо");
        BUTTONS1.put("settings_btn", "Налаштування");

        BUTTONS2.put("decimalpoint_btn", "Кількість знаків після коми");
        BUTTONS2.put("bank_btn", "Банк");
        BUTTONS2.put("currency_btn", "Валюти");
        BUTTONS2.put("notification_btn", "Час оповіщень");

        BUTTONS3.put("decimalpoint_btn1", "2");
        BUTTONS3.put("decimalpoint_btn2", "3");
        BUTTONS3.put("decimalpoint_btn3", "4");

        BUTTONS4.put("bank_btn1", "НБУ");
        BUTTONS4.put("bank_btn2", "ПриватБанк");
        BUTTONS4.put("bank_btn3", "Монобанк");

        BUTTONS5.put("currency_btn1", "✅USD");
        BUTTONS5.put("currency_btn2", "EUR");

        BUTTONS6.put("notification_btn1", "9");
        BUTTONS6.put("notification_btn2", "10");
        BUTTONS6.put("notification_btn3", "11");
        BUTTONS6.put("notification_btn4", "12");
        BUTTONS6.put("notification_btn5", "13");
        BUTTONS6.put("notification_btn6", "14");
        BUTTONS6.put("notification_btn7", "15");
        BUTTONS6.put("notification_btn8", "16");
        BUTTONS6.put("notification_btn9", "17");
        BUTTONS6.put("notification_btn10", "18");
        BUTTONS6.put("notification_btn11", "Вимкнути повідомлення");

//        BUTTONS6.put("9", "notification_btn1");
//        BUTTONS6.put("10", "notification_btn2");
//        BUTTONS6.put("11", "notification_btn3");
//        BUTTONS6.put("12", "notification_btn4");
//        BUTTONS6.put("13", "notification_btn5");
//        BUTTONS6.put("14", "notification_btn6");
//        BUTTONS6.put("15", "notification_btn7");
//        BUTTONS6.put("16", "notification_btn8");
//        BUTTONS6.put("17", "notification_btn9");
//        BUTTONS6.put("18", "notification_btn10");
//        BUTTONS6.put("Вимкнути повідомлення", "notification_btn11");
    }
}
