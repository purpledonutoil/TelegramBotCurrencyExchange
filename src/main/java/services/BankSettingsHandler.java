package services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import storage.UserSettings;

import java.util.*;

public class BankSettingsHandler {

    private final Map<Long, UserSettings> userSettingsMap;

    public BankSettingsHandler(Map<Long, UserSettings> userSettingsMap) {
        this.userSettingsMap = userSettingsMap;
    }

    public SendMessage getBankSelectionMessage(Long chatId, Long userId) {
        UserSettings settings = userSettingsMap.getOrDefault(userId, new UserSettings());
        String currentBank = settings.getBank();

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(List.of(createButton("NBU", currentBank)));
        rows.add(List.of(createButton("PrivatBank", currentBank)));
        rows.add(List.of(createButton("Monobank", currentBank)));

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(rows).build();

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Оберіть банк:")
                .replyMarkup(markup)
                .build();
    }

    public void updateBankSelection(Long userId, String selectedBank) {
        UserSettings settings = userSettingsMap.getOrDefault(userId, new UserSettings());
        settings.setBank(selectedBank);
        userSettingsMap.put(userId, settings);
    }

    private InlineKeyboardButton createButton(String bankName, String currentBank) {
        String label = bankName.equals(currentBank) ? "✅ " + bankName : bankName;
        return InlineKeyboardButton.builder()
                .text(label)
                .callbackData("bank_" + bankName)
                .build();
    }
}

