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

            // Обробка кнопки налаштувань
            if (callbackData.equals("settings_btn")) {
                this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            }

            // Обробка кнопок, які змінюють валюту, десяткові знаки, банки
            if (callbackData.equals("decimalpoint_btn")) {
                this.sendMessage(chatID, MESSAGE3, BUTTONS3);
            }
            if (callbackData.equals("bank_btn")) {
                this.sendMessage(chatID, MESSAGE4, BUTTONS4);
            }
            if (callbackData.equals("currency_btn")) {
                this.sendMessage(chatID, MESSAGE5, BUTTONS5);
            }

            // Обробка кнопки налаштувань сповіщень
            if (callbackData.equals("notification_btn")) {
                this.sendNotificationTimeSettings(chatID);
            }

            // Якщо натискається кнопка для конкретного часу сповіщень
            if (callbackData.startsWith("set_notification_time_")) {
                int selectedHour = Integer.parseInt(callbackData.split("_")[3]);
                this.setNotificationTime(chatID, selectedHour);
            }

            // Обробка кнопки для вимкнення сповіщень
            if (callbackData.equals("disable_notifications")) {
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
            e.printStackTrace();
        }
        return 0;
    }

    // Додамо метод для відправки налаштувань часу сповіщень
    public void sendNotificationTimeSettings(Long chatID) {
        String[] hours = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};
        StringBuilder messageText = new StringBuilder("⏰ Обери час, коли хочеш отримувати щоденні сповіщення:\n");

        // Створення кнопок для вибору часу
        for (String hour : hours) {
            messageText.append(hour).append("\n");
        }
        messageText.append("🔕 Вимкнути сповіщення");

        Map<String, String> buttons = new HashMap<>();
        buttons.put("set_notification_time_9", "09:00");
        buttons.put("set_notification_time_10", "10:00");
        buttons.put("set_notification_time_11", "11:00");
        buttons.put("set_notification_time_12", "12:00");
        buttons.put("set_notification_time_13", "13:00");
        buttons.put("set_notification_time_14", "14:00");
        buttons.put("set_notification_time_15", "15:00");
        buttons.put("set_notification_time_16", "16:00");
        buttons.put("set_notification_time_17", "17:00");
        buttons.put("set_notification_time_18", "18:00");
        buttons.put("disable_notifications", "Вимкнути");
        this.sendMessage(chatID, messageText.toString(), buttons);
    }

    // Метод для встановлення часу сповіщення
    public void setNotificationTime(Long chatID, int hour) {
        UserSettings settings = userSettingsMap.getOrDefault(chatID, new UserSettings());
        settings.setNotificationTime(hour);
        userSettingsMap.put(chatID, settings);
        this.sendMessage(chatID, "Тепер ваше сповіщення буде приходити о " + (hour == -1 ? "вимкнено" : hour + ":00"), BUTTONS1);
    }

    @Override
    public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {
        // not released yet
    }
}
