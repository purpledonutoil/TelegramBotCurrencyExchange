package utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
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
        if (buttons != null && !buttons.isEmpty()) {
            if (buttons.size()<6){
                InlineKeyboardMarkup markup = attachButtons(buttons);
                message.setReplyMarkup(markup);
            }
            else{
                attachKeyboard(message, buttons);
            }
        }
        return message;
    }

    private static InlineKeyboardMarkup attachButtons(Map<String, String> buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getValue());
            button.setCallbackData(entry.getKey());
            keyboard.add(List.of(button));
        }

        markup.setKeyboard(keyboard);
        return markup;
    }

    private static void attachKeyboard(SendMessage message, Map<String, String> buttons) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        int count = 0;
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            row.add(entry.getValue());
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

    public static EditMessageReplyMarkup modifyButtons(Long chatId, int messageId, String[] values, Map<String, String> buttons) {

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            if (entry.getValue().contains("✅")){
                buttons.replace(entry.getKey(), entry.getValue().substring(1));
            }
        }
        for (String value : values) {
            for (Map.Entry<String, String> entry : buttons.entrySet()) {
                if (entry.getValue().equals(value)){
                    buttons.replace(entry.getKey(), "✅" + entry.getValue());
                }
            }
        }
        EditMessageReplyMarkup editMarkup = modifyButtons(chatId, messageId, buttons);

        return editMarkup;
    }

    public static EditMessageReplyMarkup modifyButtons(Long chatId, int messageId, Map<String, String> buttons) {
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(chatId);
        editMarkup.setMessageId(messageId);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            InlineKeyboardButton newButton = new InlineKeyboardButton();
            newButton.setText(entry.getValue());
            newButton.setCallbackData(entry.getKey());
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(newButton);
            keyboard.add(List.of(newButton));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        editMarkup.setReplyMarkup(markup);

        return editMarkup;
    }
    public static EditMessageText modifyText(Long chatId, int messageId, String newText, Map<String, String> newButtons) {
        InlineKeyboardMarkup markup = null;
        markup = attachButtons(newButtons);

        EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(newText);
        editMessage.setReplyMarkup(markup);

        return editMessage;
    }
}
