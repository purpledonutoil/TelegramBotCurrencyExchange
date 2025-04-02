package org.telegrambot.executor;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.Map;

public interface TelegramBotUtils {
    Long getChatId(Update update);
    SendMessage createMessage(Long chatId, String text);
    SendMessage createMessage(Long chatId, String text, Map<String, String> buttons);
    void attachButtons(SendMessage message, Map<String, String> buttons);
    void attachKeyboard(SendMessage message, Map<String, String> buttons);
    void removeKeyboard(SendMessage message);
    void main(String[] args);
}