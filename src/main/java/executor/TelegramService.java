package executor;

import executor.commands.TelegramBotUtils;
import storage.Storage;
import storage.UserSettings;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.HashMap;
import java.util.Map;

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
                this.sendMessage(chatID, MESSAGE1, BUTTONS1);
            }
    
            if (messageReader.get(chatID) == Boolean.TRUE) {
                if (messageText.matches("^(1[0-8]|[9])$")) {
                    int selectedHour = Integer.parseInt(messageText);
    
                    UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                    settings.setNotificationTime(selectedHour);
                    Storage.getInstance().saveUserSettings(chatID, settings);
    
                    int messageId = this.sendMessage(chatID, "Тепер ваше повідомлення буде приходити о " + selectedHour + ":00", BUTTONS1);
                    deleteMessage(chatID, Map.of(chatID, messageId));
                } else if (messageText.equalsIgnoreCase("Вимкнути повідомлення")) {
                    UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                    settings.setNotificationTime(-1);
                    Storage.getInstance().saveUserSettings(chatID, settings);
    
                    int messageId = this.sendMessage(chatID, "Повідомлення вимкнено.", BUTTONS1);
                    deleteMessage(chatID, Map.of(chatID, messageId));
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
                this.sendMessage(chatID, MESSAGE5, BUTTONS5);
            }

            if (callbackData.equals("notification_btn")) {
                this.sendMessage(chatID, MESSAGE6, BUTTONS6);
                messageReader.put(chatID, true);
            }

            if (callbackData.equals("info_btn")) {
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
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

@Override
public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {
    Integer messageId = lastMessageIds.get(chatID);
    if (messageId != null) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatID.toString());
        deleteMessage.setMessageId(messageId);
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            System.err.println("Error deleting message: " + e.getMessage());
        }
    }
}

}
