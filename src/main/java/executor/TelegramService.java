package executor;

import executor.commands.TelegramBotUtils;
import storage.UserSettings;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import services.NotificationService;

import java.util.HashMap;
import java.util.Map;

import static executor.TelegramBotContent.*;

public class TelegramService extends TelegramLongPollingBot implements TelegramBot {
    private static String botName;

    // Збереження налаштувань користувачів
    private Map<Long, UserSettings> userSettingsMap;

    @Override
    public String getBotUsername() {
        return botName;
    }

    public TelegramService(String botToken, String botName, Map<Long, UserSettings> userSettingsMap) {
        super(botToken);
        TelegramService.botName = botName;
        this.userSettingsMap = userSettingsMap;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Long chatID = TelegramBotUtils.getChatId(update);

        // Обробка команди /start
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            this.sendMessage(chatID, MESSAGE1, BUTTONS1);
            return;
        }

        // Обробка текстових повідомлень (вибір часу)
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            handleTextInput(chatID, text);
            return;
        }

        // Обробка callback-кнопок
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();

            switch (callbackData) {
                case "settings_btn":
                    this.sendMessage(chatID, MESSAGE2, BUTTONS2);
                    break;
                case "decimalpoint_btn":
                    this.sendMessage(chatID, MESSAGE3, BUTTONS3);
                    break;
                case "bank_btn":
                    this.sendMessage(chatID, MESSAGE4, BUTTONS4);
                    break;
                case "currency_btn":
                    this.sendMessage(chatID, MESSAGE5, BUTTONS5);
                    break;
                case "notification_btn":
                    // Надсилаємо клавіатуру з NotificationService
                    int currentHour = userSettingsMap.getOrDefault(chatID, new UserSettings()).getNoticeTime();
                    NotificationService notificationService = new NotificationService(this);
                    notificationService.sendNotificationTimeSettings(chatID, currentHour);
                    break;
            }
        }
    }

    private void handleTextInput(Long chatID, String text) {
        // Перевіряємо, чи текст — це одна з опцій годин
        String[] validHours = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

        for (String hour : validHours) {
            if (text.equals(hour)) {
                int hourInt = Integer.parseInt(hour.split(":")[0]);
                setNotificationTime(chatID, hourInt);
                return;
            }
        }

        // Обробка вимкнення сповіщень
        if (text.equals("🔕 Вимкнути сповіщення") || text.equals("🔕 Сповіщення вимкнено")) {
            setNotificationTime(chatID, -1);
        }
    }

    public void setNotificationTime(Long chatID, int hour) {
        UserSettings settings = userSettingsMap.getOrDefault(chatID, new UserSettings());
        settings.setNotificationTime(hour);
        userSettingsMap.put(chatID, settings);

        String message = (hour == -1)
                ? "🔕 Сповіщення вимкнено"
                : "✅ Тепер ваше сповіщення буде приходити о " + hour + ":00";

        this.sendMessage(chatID, message, BUTTONS1);
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
        // not implemented yet
    }
}
