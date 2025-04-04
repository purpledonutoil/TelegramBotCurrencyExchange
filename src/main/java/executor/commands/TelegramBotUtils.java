package executor.commands;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TelegramBotUtils {
    private TelegramBotUtils() {
    }

    public static Long getChatId(Update update) {
        if (update.hasMessage()) return update.getMessage().getFrom().getId();
        if (update.hasCallbackQuery()) return update.getCallbackQuery().getFrom().getId();
        return null;
    }


    public static SendMessage createMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setParseMode("markdown");
        message.setChatId(chatId);
        return message;
    }

    public static SendMessage createMessage(Long chatId, String text, Map<String, String> buttons) {
        SendMessage message = createMessage(chatId, text);
        removeKeyboard(message);
        if (buttons != null && !buttons.isEmpty()) {
            if (buttons.size()<6){
                attachButtons(message, buttons);
            }
            else{
                attachKeyboard(message, buttons);
            }
        }
        return message;
    }

    private static void attachButtons(SendMessage message, Map<String, String> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData(entry.getValue());
            keyboard.add(List.of(button));
        }

        markup.setKeyboard(keyboard);
        message.setReplyMarkup(markup);
    }

    private static void attachKeyboard(SendMessage message, Map<String, String> buttons) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        int count = 0;
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            row.add(entry.getKey());
            count++;
            if (count%3==0){
                keyboard.add(row);
                row = new KeyboardRow();
            }
        }
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
    }

    private static void removeKeyboard(SendMessage message) {
        if (message.getReplyMarkup() instanceof ReplyKeyboardMarkup) {
            ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();
            keyboardRemove.setRemoveKeyboard(true);
            message.setReplyMarkup(keyboardRemove);

            message.setText("");
        }
    }
}
