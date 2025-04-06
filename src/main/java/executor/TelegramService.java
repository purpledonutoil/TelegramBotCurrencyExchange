package executor;

import executor.commands.TelegramBotUtils;
import storage.Storage;
import storage.UserSettings;

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

        // Якщо користувач написав /start
        if (update.hasMessage()) {
            String messageText = update.getMessage().getText();

            // Якщо користувач написав /start
            if (messageText.equals("/start")) {
                this.sendMessage(chatID, MESSAGE1, BUTTONS1);
            }

            // Якщо повідомлення користувача містить час для сповіщень (9-18)
            if (messageText.matches("^(1[0-8]|[9])$")) {
                int selectedHour = Integer.parseInt(messageText);

                // Заміна прямого виклику setNotificationTime
                UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                settings.setNotificationTime(selectedHour);
                Storage.getInstance().saveUserSettings(chatID, settings); // Збереження змін у Storage

                this.sendMessage(chatID, "Тепер ваше повідомлення буде приходити о " + (selectedHour == -1 ? "вимкнено" : selectedHour + ":00"), BUTTONS1);
            }

            // Якщо користувач хоче вимкнути повідомлення
            else if (messageText.equalsIgnoreCase("Вимкнути повідомлення")) {
                // Заміна прямого виклику setNotificationTime
                UserSettings settings = Storage.getInstance().getUserSettings(chatID);
                settings.setNotificationTime(-1); // -1 означає вимкнення повідомлень
                Storage.getInstance().saveUserSettings(chatID, settings); // Збереження змін у Storage

                this.sendMessage(chatID, "Повідомлення вимкнено.", BUTTONS1);
            }
        }

        // Обробка callback кнопок
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            if (callbackData.equals("settings_btn")) {
                // Обробка кнопки налаштувань
                this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            }

            if (callbackData.equals("decimalpoint_btn")) {
                // Обробка кнопки, яка змінює десяткові знаки
                this.sendMessage(chatID, MESSAGE3, BUTTONS3);
            }

            if (callbackData.equals("bank_btn")) {
                // Обробка кнопки для вибору банку
                this.sendMessage(chatID, MESSAGE4, BUTTONS4);
            }

            if (callbackData.equals("currency_btn")) {
                // Обробка кнопки для вибору валюти
                this.sendMessage(chatID, MESSAGE5, BUTTONS5);
            }

            if (callbackData.equals("notification_btn")) {
                // Обробка кнопки налаштувань сповіщень
                this.sendMessage(chatID, MESSAGE6, BUTTONS6);
            }

            if (callbackData.equals("info_btn")) {
                // Обробка кнопки для отримання інформації
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
        //not released yet
    }
}
