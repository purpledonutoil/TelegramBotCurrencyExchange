package executor;

import banking.Bank;
import banking.BankService;
import banking.Currency;
import banking.CurrencyRate;
import executor.commands.TelegramBotUtils;
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
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            // write new user and default settings to the Storage
            UserSettings userSettings = new UserSettings();
//            // for testing purpose
//            userSettings.addBank(Bank.NBU);
//            userSettings.addBank(Bank.MONO);
//            userSettings.addBank(Bank.PRIVAT);
//            userSettings.addCurrency(Currency.USD);
//            userSettings.addCurrency(Currency.EUR);
//            userSettings.setRoundNumber(3);
            Storage.getInstance().saveUserSettings(chatID, userSettings);

            this.sendMessage(chatID, MESSAGE1, BUTTONS1);
        }
        if (update.hasCallbackQuery()) {

            if (update.getCallbackQuery().getData().equals("settings_btn")) {
                this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            }

            if (update.getCallbackQuery().getData().equals("decimalpoint_btn")) {
                int messageId = this.sendMessage(chatID, MESSAGE3, BUTTONS3);
                lastMessageIds.put(chatID, messageId);
            }
            if (update.getCallbackQuery().getData().equals("bank_btn")) {
                int messageId = this.sendMessage(chatID, MESSAGE4, BUTTONS4);
                lastMessageIds.put(chatID, messageId);
            }
            if (update.getCallbackQuery().getData().equals("currency_btn")) {
                int messageId = this.sendMessage(chatID, MESSAGE5, BUTTONS5);
                lastMessageIds.put(chatID, messageId);
            }
            if (update.getCallbackQuery().getData().equals("notification_btn")) {
                this.sendMessage(chatID, MESSAGE6, BUTTONS6);
            }

            if (update.getCallbackQuery().getData().equals("info_btn")) {

                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
                Map<Bank, List<CurrencyRate>> bankRates = BankService.getBankRates(userSettings);
                String prettyTextMessage = InfoMessage.getPrettyMessage(bankRates, userSettings.getRoundNumber());

                this.sendInfoMessage(chatID, prettyTextMessage);
            }

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
        //not released yet
    }

    public int sendInfoMessage(Long chatID, String textMessage){
        return this.sendMessage(chatID, textMessage, BUTTONS1);
    }
}
