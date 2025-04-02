package org.telegrambot.executor;

import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.HashMap;
import java.util.Map;

public interface TelegramBot {
    String botUsername = "";
    Map<Long, Integer> lastMessageIds = new HashMap<>();
    Map<Long, Integer> lastSettingsMessage = new HashMap<>();
    String getBotUsername();
    void onUpdateReceived(Update update);
    int sendMessage(Long chatID, String text, Map<String, String> buttons);
    void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds);
}
