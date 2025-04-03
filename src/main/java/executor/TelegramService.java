package executor;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

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
        Long chatID = getChatId(update);
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            this.sendMessage(chatID, "Ласкаво просимо. Цей бот допоможе відслідковувати актуальні курси валют.", Map.of());
        }
    }

    public static Long getChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getFrom().getId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getFrom().getId();
        return null;
    }

    @Override
    public int sendMessage(Long chatID, String text, Map<String, String> buttons) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setParseMode("markdown");
        message.setChatId(chatID);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {

    }
}
