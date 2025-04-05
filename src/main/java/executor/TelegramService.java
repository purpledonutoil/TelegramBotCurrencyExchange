package executor;

import executor.commands.TelegramBotUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

import static executor.TelegramBotContent.*;

public class TelegramService extends TelegramLongPollingBot implements TelegramBot {
    private static String botName;

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
            this.sendMessage(chatID, MESSAGE1, BUTTONS1);
        }
        if (update.hasCallbackQuery()) {

            if (update.getCallbackQuery().getData().equals("settings_btn")) {
                this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            }

            if (update.getCallbackQuery().getData().equals("decimalpoint_btn")) {
                this.sendMessage(chatID, MESSAGE3, BUTTONS3);
            }
            if (update.getCallbackQuery().getData().equals("bank_btn")) {
                this.sendMessage(chatID, MESSAGE4, BUTTONS4);
            }
            if (update.getCallbackQuery().getData().equals("currency_btn")) {
                this.sendMessage(chatID, MESSAGE5, BUTTONS5);
            }
            if (update.getCallbackQuery().getData().equals("notification_btn")) {
                this.sendMessage(chatID, MESSAGE6, BUTTONS6);
            }

            if (update.getCallbackQuery().getData().equals("info_btn")) {
                this.sendMessage(chatID, MESSAGE7, BUTTONS1);
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
}
