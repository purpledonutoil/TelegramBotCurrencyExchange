package executor;

import executor.commands.TelegramBotUtils;
import storage.UserSettings;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

import static executor.TelegramBotContent.*;

public class TelegramService extends TelegramLongPollingBot implements TelegramBot {
    private static String botName;

    // Можна додати якийсь клас для збереження даних користувача (наприклад, UserSettings)
    private Map<Long, UserSettings> userSettingsMap;

    @Override
    public String getBotUsername() {
        return botName;
    }

    public TelegramService(String botToken, String botName) {
        super(botToken);
        this.botName = botName;
    }

    public TelegramService(String botToken, String botName, Map<Long, UserSettings> userSettingsMap) {
        super(botToken);
        this.botName = botName;
        this.userSettingsMap = userSettingsMap;  // Збереження налаштувань користувачів
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatID = TelegramBotUtils.getChatId(update);

        // Якщо користувач написав /start
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            this.sendMessage(chatID, MESSAGE1, BUTTONS1);
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

        // Якщо користувач написав повідомлення
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            // Якщо повідомлення користувача містить час для сповіщень (9-18)
            if (messageText.matches("^(1[0-8]|[9])$")) {
                int selectedHour = Integer.parseInt(messageText);
                this.setNotificationTime(chatID, selectedHour);
            }

            // Якщо користувач хоче вимкнути сповіщення
            else if (messageText.equalsIgnoreCase("Вимкнути сповіщення")) {
                this.setNotificationTime(chatID, -1);  // -1 означає вимкнення сповіщень
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

    // Метод для встановлення часу сповіщень
    public void setNotificationTime(Long chatID, int hour) {
        UserSettings settings = userSettingsMap.getOrDefault(chatID, new UserSettings());
        settings.setNotificationTime(hour);
        userSettingsMap.put(chatID, settings);
        this.sendMessage(chatID, "Тепер ваше сповіщення буде приходити о " + (hour == -1 ? "вимкнено" : hour + ":00"), BUTTONS1);
    }

    @Override
    public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {
        //not released yet
        // not released yet
    }
}
