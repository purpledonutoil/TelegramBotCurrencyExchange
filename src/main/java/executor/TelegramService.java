package executor;

import banking.Bank;
import banking.BankService;
import banking.Currency;
import banking.CurrencyRate;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
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
    private static final Map<Long, Integer> lastMessageIds = new HashMap<>();
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
//                // for testing purpose
//                userSettings.addBank(Bank.NBU);
//                userSettings.addBank(Bank.MONO);
//                userSettings.addBank(Bank.PRIVAT);
//                userSettings.addCurrency(Currency.USD);
//                userSettings.addCurrency(Currency.EUR);
//                userSettings.setRoundNumber(3);
                Storage.getInstance().saveUserSettings(chatID, userSettings);
                this.sendMessage(chatID, MESSAGE1, BUTTONS1);
            }


            String messageText = update.getMessage().getText();
            if (Boolean.TRUE.equals(messageReader.get(chatID))) {
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
            String bank = update.getCallbackQuery().getData().equals("bank_btn1") ? "NBU" :
                    update.getCallbackQuery().getData().equals("bank_btn2") ? "PRIVAT" :
                            update.getCallbackQuery().getData().equals("bank_btn3") ? "MONO" :null;
            if (bank != null) {
                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
                EnumSet<Bank> banks = userSettings.setBank(Bank.valueOf(bank));
                String[] enumArray = banks.stream()
                        .map(Bank::getTitle)
                        .toArray(String[]::new);

                this.modifyButtons(chatID, lastMessageIds.get(chatID), enumArray, BUTTONS4);
            }
            if (update.getCallbackQuery().getData().startsWith("currency_btn") &&
                    !update.getCallbackQuery().getData().equals("currency_btn")) {

                UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                Currency selected = getCurrencyFromCallback(update.getCallbackQuery().getData());
                settings.setCurrency(selected);
                Storage.getInstance().saveUserSettings(chatID, settings);

                EnumSet<Currency> selectedCurrencies = settings.getCurrencies();
                String[] selectedValues = selectedCurrencies.stream()
                        .map(Currency::name)
                        .toArray(String[]::new);

                this.modifyButtons(
                        chatID,
                        lastMessageIds.get(chatID),
                        selectedValues,
                        BUTTONS5
                );
            }
            //deleteMessage(chatID, lastMessageIds);
            if (update.getCallbackQuery().getData().equals("settings_btn")) {
                if (lastSettingsMessageIds.get(chatID)==null){
                    int messageId = this.sendMessage(chatID, MESSAGE2, BUTTONS2);
                    lastSettingsMessageIds.put(chatID, messageId);
                }
            }
            if (update.getCallbackQuery().getData().equals("decimalpoint_btn")) {
                if (lastMessageIds.get(chatID)==null){
                    int messageId = this.sendMessage(chatID, MESSAGE3, BUTTONS3);
                    lastMessageIds.put(chatID, messageId);
                } else {
                    this.modifyMessage(chatID, lastMessageIds.get(chatID), MESSAGE3, BUTTONS3);
                }
            }
            if (update.getCallbackQuery().getData().equals("bank_btn")) {
                if (lastMessageIds.get(chatID)==null){
                    int messageId = this.sendMessage(chatID, MESSAGE4, BUTTONS4);
                    lastMessageIds.put(chatID, messageId);
                } else {
                    this.modifyMessage(chatID, lastMessageIds.get(chatID), MESSAGE4, BUTTONS4);
                }
            }
            if (update.getCallbackQuery().getData().equals("currency_btn")) {
                if (lastMessageIds.get(chatID)==null){
                    int messageId = this.sendMessage(chatID, MESSAGE5, BUTTONS5);
                    lastMessageIds.put(chatID, messageId);
                } else {
                    this.modifyMessage(chatID, lastMessageIds.get(chatID), MESSAGE5, BUTTONS5);
                }
            }
            if (update.getCallbackQuery().getData().equals("notification_btn")) {
                int messageId = this.sendMessage(chatID, MESSAGE6, BUTTONS6);
                lastMessageIds.put(chatID, messageId);
                messageReader.put(chatID, true);
            }

            if (update.getCallbackQuery().getData().equals("info_btn")) {
                deleteMessage(chatID, lastSettingsMessageIds);
                deleteMessage(chatID, lastMessageIds);

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

    public void modifyMessage(Long chatID, int messageId, String message, Map<String, String> buttons){
//        if (buttons.size()>=5){
//            this.deleteMessage(chatID, lastMessageIds);
//            this.sendMessage(chatID, message, buttons);
//        }

        EditMessageReplyMarkup editMarkup = TelegramBotUtils.modifyButtons(chatID, messageId, buttons);

        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void modifyButtons(Long chatID, int messageId, String[] values, Map<String, String> buttons){
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
}
