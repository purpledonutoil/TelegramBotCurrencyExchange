package services;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private TelegramLongPollingBot telegramBot;

    public NotificationService(TelegramLongPollingBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendNotificationTimeSettings(Long chatId, int currentHour) {
        SendMessage message = createNotificationMessage(chatId, currentHour);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            handleTelegramApiException(e);
        }
    }

    private SendMessage createNotificationMessage(Long chatId, int currentHour) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("⏰ Обери час, коли хочеш отримувати щоденні повідомлення:");

        ReplyKeyboardMarkup keyboardMarkup = createKeyboardMarkup(currentHour);
        message.setReplyMarkup(keyboardMarkup);

        return message;
    }

    private ReplyKeyboardMarkup createKeyboardMarkup(int currentHour) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> rows = new ArrayList<>();
        String[] hours = getAvailableHours();

        for (int i = 0; i < hours.length; i += 3) {
            KeyboardRow row = new KeyboardRow();
            for (int j = 0; j < 3 && (i + j) < hours.length; j++) {
                String hour = hours[i + j];
                String buttonLabel = (Integer.parseInt(hour.split(":")[0]) == currentHour) 
                        ? "✅ " + hour 
                        : hour;
                row.add(buttonLabel);
            }
            rows.add(row);
        }

        KeyboardRow offRow = new KeyboardRow();
        String offButtonText = (currentHour == -1) ? "🔕 Повідомлення вимкнено" : "🔕 Вимкнути повідомлення";
        offRow.add(offButtonText);
        rows.add(offRow);

        keyboardMarkup.setKeyboard(rows);
        return keyboardMarkup;
    }

    private String[] getAvailableHours() {
        return new String[]{"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};
    }

    private void handleTelegramApiException(TelegramApiException e) {
        System.err.println("Error occurred while sending message: " + e.getMessage());
        e.printStackTrace();
    }
}