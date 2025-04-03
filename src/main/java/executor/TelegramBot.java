package executor;

import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.HashMap;
import java.util.Map;

public interface TelegramBot {
    String getBotUsername();
    void onUpdateReceived(Update update);
    int sendMessage(Long chatID, String text, Map<String, String> buttons);
    void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds);
}
