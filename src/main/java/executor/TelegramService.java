package executor;

import banking.Bank;
import banking.BankService;
import banking.Currency;
import banking.CurrencyRate;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import utils.TelegramBotUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import storage.Storage;
import storage.UserSettings;
import utils.InfoMessage;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static executor.TelegramBotContent.*;

public class TelegramService extends TelegramLongPollingBot implements TelegramBot {
    private static String botName;
    private static final Map<Long, Integer> lastMessageWithButtonsIds = new HashMap<>();
    private static final Map<Long, Integer> lastMessageWithKeyboardIds = new HashMap<>();
    private static final Map<Long, Integer> lastSettingsMessageIds = new HashMap<>();
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
            if (update.getMessage().getText().equals("/start")) {
                // write new user and default settings to the Storage
                UserSettings userSettings = new UserSettings();
                Storage.getInstance().saveUserSettings(chatID, userSettings);
                this.sendMessage(chatID, MESSAGE1, BUTTONS1);
            }

            String messageText = update.getMessage().getText();
            if (Boolean.TRUE.equals(messageReader.get(chatID))) {
                this.changeNotificationSettings(chatID, messageText);
            }
        }

        if (update.hasCallbackQuery()) {
            String callback = update.getCallbackQuery().getData();
            this.answerPressedSettingsButton(chatID, callback);

            if (callback.equals("info_btn")) {
                deleteMessage(chatID, lastSettingsMessageIds);
                deleteMessage(chatID, lastMessageWithKeyboardIds);
                deleteMessage(chatID, lastMessageWithButtonsIds);

                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
                Map<Bank, List<CurrencyRate>> bankRates = BankService.getBankRates(userSettings);
                String prettyTextMessage = InfoMessage.getPrettyMessage(bankRates, userSettings.getRoundNumber());

                this.sendInfoMessage(chatID, prettyTextMessage);
            }
        }
    }

    private void answerPressedSettingsButton(Long chatID, String callback) {
        if (callback.equals("settings_btn")
                && lastSettingsMessageIds.get(chatID) == null) {
            int messageId = this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            lastSettingsMessageIds.put(chatID, messageId);
        }

        if (callback.equals("decimalpoint_btn")) {
            this.sendMessage(chatID, MESSAGE3, BUTTONS3, lastMessageWithButtonsIds, lastMessageWithKeyboardIds);
        }

        if (callback.equals("bank_btn")) {
            this.sendMessage(chatID, MESSAGE4, BUTTONS4, lastMessageWithButtonsIds, lastMessageWithKeyboardIds);
        }

        if (callback.equals("currency_btn")) {
            this.sendMessage(chatID, MESSAGE5, BUTTONS5, lastMessageWithButtonsIds, lastMessageWithKeyboardIds);
        }

        if (callback.equals("notification_btn")) {
            this.sendMessage(chatID, MESSAGE6, BUTTONS6, lastMessageWithKeyboardIds, lastMessageWithButtonsIds);
            messageReader.put(chatID, true);
        }

        if (callback.startsWith("bank_btn")
                && !callback.equals("bank_btn")) {
            this.changeBankSettings(chatID, callback);
        }

        if (callback.startsWith("decimalpoint_btn")
                && !callback.equals("decimalpoint_btn")) {
            this.changeDecimalPointSettings(chatID, callback);
        }

        if (callback.startsWith("currency_btn")
                && !callback.equals("currency_btn")) {
            this.changeCurrencySettings(chatID, callback);
        }
    }

    public void sendMessage(Long chatID, String text, Map<String, String> buttons, Map<Long, Integer> lastMessageIds, Map<Long, Integer> lastOtherMessageIds) {
        if (lastMessageIds.get(chatID) == null
                || lastOtherMessageIds.get(chatID) != null) {
            deleteMessage(chatID, lastOtherMessageIds);
            int messageId = this.sendMessage(chatID, text, buttons);
            lastMessageIds.put(chatID, messageId);
        } else {
            this.modifyMessage(chatID, lastMessageIds.get(chatID), text, buttons);
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

    public void modifyMessage(Long chatID, int messageId, String message, Map<String, String> buttons) {
        EditMessageText editText = TelegramBotUtils.modifyText(chatID, messageId, message, buttons);

        try {
            execute(editText);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void modifyButtons(Long chatID, int messageId, String[] values, Map<String, String> buttons) {
        EditMessageReplyMarkup editMarkup = TelegramBotUtils.modifyButtons(chatID, messageId, values, buttons);

        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {
        if (lastMessageIds.containsKey(chatID)) {
            Integer lastMessageId = lastMessageIds.get(chatID);

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatID);
            deleteMessage.setMessageId(lastMessageId);

            try {
                execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            lastMessageIds.remove(chatID);
        }
    }

    public int sendInfoMessage(Long chatID, String textMessage) {
        return this.sendMessage(chatID, textMessage, BUTTONS1);
    }

    private void changeNotificationSettings(Long chatID, String button){
        if (button.matches("^(1[0-8]|[9])$")) {
            int selectedHour = Integer.parseInt(button);

            UserSettings settings = Storage.getInstance().getUserSettings(chatID);
            settings.setNotificationTime(selectedHour);
            Storage.getInstance().saveUserSettings(chatID, settings);
        } else if (button.equalsIgnoreCase("Вимкнути повідомлення")) {
            UserSettings settings = Storage.getInstance().getUserSettings(chatID);
            settings.setNotificationTime(-1);
            Storage.getInstance().saveUserSettings(chatID, settings);
        }
    }

    private void changeBankSettings(Long chatID, String button){
        String bank = switch (button) {
            case "bank_btn1" -> "NBU";
            case "bank_btn2" -> "PRIVAT";
            case "bank_btn3" -> "MONO";
            default -> null;
        };

        if (bank != null) {
            UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
            EnumSet<Bank> banks = userSettings.setBank(Bank.valueOf(bank));
            String[] enumArray = banks.stream()
                    .map(Bank::getTitle)
                    .toArray(String[]::new);

            this.modifyButtons(chatID, lastMessageWithButtonsIds.get(chatID), enumArray, BUTTONS4);
        }
    }

    private void changeDecimalPointSettings(Long chatID, String button){
        int selectedValue = switch (button) {
            case "decimalpoint_btn1" -> 2;
            case "decimalpoint_btn2" -> 3;
            case "decimalpoint_btn3" -> 4;
            default -> -1;
        };

        UserSettings settings = Storage.getInstance().getUserSettings(chatID);
        if (settings.getRoundNumber() != selectedValue) {
            settings.setRoundNumber(selectedValue);
            String[] selectedValues = new String[]{String.valueOf(selectedValue)};

            this.modifyButtons(chatID, lastMessageWithButtonsIds.get(chatID), selectedValues, BUTTONS3);
        }
    }

    private void changeCurrencySettings(Long chatID, String button){
        UserSettings settings = Storage.getInstance().getUserSettings(chatID);
        Currency selected = switch (button) {
            case "currency_btn1" -> Currency.USD;
            case "currency_btn2" -> Currency.EUR;
            default -> null;
        };

        EnumSet<Currency> selectedCurrencies = settings.setCurrency(selected);
        String[] selectedValues = selectedCurrencies.stream()
                .map(Currency::name)
                .toArray(String[]::new);

        this.modifyButtons(chatID, lastMessageWithButtonsIds.get(chatID), selectedValues, BUTTONS5);
    }
}



