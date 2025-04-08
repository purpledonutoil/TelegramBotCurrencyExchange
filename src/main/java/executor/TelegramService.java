package executor;

import banking.Bank;
import banking.BankService;
import banking.Currency;
import banking.CurrencyRate;
import executor.commands.TelegramBotUtils;
import storage.Storage;
import storage.UserSettings;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import storage.Storage;
import storage.UserSettings;
import utils.InfoMessage;

import java.util.*;

import static executor.TelegramBotContent.*;

public class TelegramService extends TelegramLongPollingBot implements TelegramBot {
    private static String botName;
    private final Map<Long, Boolean> messageReader = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botName;
    }

    public TelegramService(String botToken, String botName) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatID = TelegramBotUtils.getChatId(update);

        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();

            if (messageText.equals("/start")) {
                UserSettings userSettings = new UserSettings();
//              // for testing purpose
//              userSettings.addBank(Bank.NBU);
//              userSettings.addBank(Bank.MONO);
//              userSettings.addBank(Bank.PRIVAT);
//              userSettings.addCurrency(Currency.USD);
//              userSettings.addCurrency(Currency.EUR);
//              userSettings.setRoundNumber(3);
                Storage.getInstance().saveUserSettings(chatID, userSettings);

                this.sendMessage(chatID, MESSAGE1, BUTTONS1);
            }

            if (messageReader.get(chatID) == true) {
                if (messageText.matches("^(1[0-8]|[9])$")) {
                    int selectedHour = Integer.parseInt(messageText);
                    UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                    settings.setNotificationTime(selectedHour);
                    Storage.getInstance().saveUserSettings(chatID, settings);
                } else if (messageText.equalsIgnoreCase("Вимкнути повідомлення")) {
                    UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                    settings.setNotificationTime(-1);
                    Storage.getInstance().saveUserSettings(chatID, settings);
                }
            }
        }

        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            messageReader.put(chatID, false);

            if (callbackData.equals("settings_btn")) {
                this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            }

            if (callbackData.equals("decimalpoint_btn")) {
                this.sendMessage(chatID, MESSAGE3, BUTTONS3);
            }

            if (callbackData.equals("bank_btn")) {
                this.sendMessage(chatID, MESSAGE4, BUTTONS4);
            }

            if (callbackData.equals("currency_btn")) {
                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
                this.sendMessage(chatID, MESSAGE5, getCurrencyButtons(userSettings));
            }

            if (callbackData.startsWith("currency_btn") && !callbackData.equals("currency_btn")) {
                UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                Set<Currency> currencies = new HashSet<>(settings.getCurrencies());

                Currency selected = getCurrencyFromCallback(callbackData);
                settings.setCurrency(selected);

                Storage.getInstance().saveUserSettings(chatID, settings);

                this.sendMessage(chatID, MESSAGE5, getCurrencyButtons(settings));
            }

            if (callbackData.equals("notification_btn")) {
                this.sendMessage(chatID, MESSAGE6, BUTTONS6);
                messageReader.put(chatID, true);
            }

            if (update.getCallbackQuery().getData().equals("info_btn")) {

                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
                Map<Bank, List<CurrencyRate>> bankRates = BankService.getBankRates(userSettings);
                String prettyTextMessage = InfoMessage.getPrettyMessage(bankRates, userSettings.getRoundNumber());

                this.sendInfoMessage(chatID, prettyTextMessage);
            }
        }
    }

    @Override
    public int sendMessage(Long chatID, String text, Map<String, String> buttons) {
        SendMessage message = TelegramBotUtils.createMessage(chatID, text, buttons);

        try {
            return execute(message).getMessageId();
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {
        //not released yet
    }

    public int sendInfoMessage(Long chatID, String textMessage){
        return this.sendMessage(chatID, textMessage, BUTTONS1);
    }
    private Currency getCurrencyFromCallback(String callback) {
        return switch (callback) {
            case "currency_btn1" -> Currency.USD;
            case "currency_btn2" -> Currency.EUR;
            default -> throw new IllegalArgumentException("Unknown currency callback: " + callback);
        };
    }

    private String getCallbackFromCurrency(Currency currency) {
        return switch (currency) {
            case USD -> "currency_btn1";
            case EUR -> "currency_btn2";
            default -> throw new IllegalArgumentException("Unknown currency: " + currency);
        };
    }

    private Map<String, String> getCurrencyButtons(UserSettings settings) {
        Map<String, String> buttons = new LinkedHashMap<>();

        for (Currency currency : List.of(Currency.USD, Currency.EUR)) {
            boolean selected = settings.getCurrencies().contains(currency);
            String label = (selected ? "✅ " : "") + currency.name();
            buttons.put(label, getCallbackFromCurrency(currency));
        }

        return buttons;
    }
}
