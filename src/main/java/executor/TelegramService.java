package executor;

import banking.Bank;
import banking.BankService;
import banking.Currency;
import banking.CurrencyRate;
import utils.TelegramBotUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import storage.Storage;
import storage.UserSettings;
import utils.InfoMessage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static executor.TelegramBotContent.*;

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
        Long chatID = TelegramBotUtils.getChatId(update);
        if (update.hasMessage() && update.getMessage().getText().equals("/start")) {
            // write new user and default settings to the Storage
            UserSettings userSettings = new UserSettings();
//            // for testing purpose
//            userSettings.addBank(Bank.NBU);
//            userSettings.addBank(Bank.MONO);
//            userSettings.addBank(Bank.PRIVAT);
//            userSettings.addCurrency(Currency.USD);
//            userSettings.addCurrency(Currency.EUR);
//            userSettings.setRoundNumber(3);
            Storage.getInstance().saveUserSettings(chatID, userSettings);

            this.sendMessage(chatID, MESSAGE1, BUTTONS1);
        }

        if (update.hasCallbackQuery()) {

            if (update.getCallbackQuery().getData().equals("settings_btn")) {
                this.sendMessage(chatID, MESSAGE2, BUTTONS2);
            }

            if (update.getCallbackQuery().getData().equals("decimalpoint_btn")) {
                this.sendMessage(chatID, MESSAGE3, BUTTONS3);
            }
            if (update.getCallbackQuery().getData().equals("bank_btn")) {
                this.sendMessage(chatID, MESSAGE4, BUTTONS4);
            }
            if (update.getCallbackQuery().getData().equals("currency_btn")) {
                this.sendMessage(chatID, MESSAGE5, BUTTONS5);
            }
            if (update.getCallbackQuery().getData().equals("notification_btn")) {
                this.sendMessage(chatID, MESSAGE6, BUTTONS6);
            }

            if (update.getCallbackQuery().getData().equals("info_btn")) {

                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);
                Map<Bank, List<CurrencyRate>> bankRates = BankService.getBankRates(userSettings);
                String prettyTextMessage = InfoMessage.getPrettyMessage(bankRates, userSettings.getRoundNumber());

                this.sendInfoMessage(chatID, prettyTextMessage);
            }
            if (update.getCallbackQuery().getData().startsWith("decimalpoint_btn")) {
                int selectedValue = switch (update.getCallbackQuery().getData()) {
                    case "decimalpoint_btn1" -> 2;
                    case "decimalpoint_btn2" -> 3;
                    case "decimalpoint_btn3" -> 4;
                    default -> throw new IllegalStateException("Unexpected value");
                };

                UserSettings settings = Storage.getInstance().getUserSettings(chatID);

                if (settings.getRoundNumber() != selectedValue) {
                    settings.setRoundNumber(selectedValue);

                    int index = selectedValue - 2;
                    int messageId = update.getCallbackQuery().getMessage().getMessageId();

                    EditMessageReplyMarkup editMarkup = TelegramBotUtils.modifyButtons(
                            chatID,
                            messageId,
                            new String[]{String.valueOf(selectedValue)},
                            TelegramBotContent.BUTTONS3
                    );

                    if (editMarkup != null) {
                        try {
                            execute(editMarkup);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (update.getCallbackQuery().getData().startsWith("decimalpoint_btn")) {
                int selectedDecimal = switch (update.getCallbackQuery().getData()) {
                    case "decimalpoint_btn1" -> 2;
                    case "decimalpoint_btn2" -> 3;
                    case "decimalpoint_btn3" -> 4;
                    default -> UserSettings.DEFAULT_DECIMAL_PLACE;
                };

                UserSettings userSettings = Storage.getInstance().getUserSettings(chatID);

                if (userSettings.getRoundNumber() == selectedDecimal) {
                    return;
                }

                userSettings.setRoundNumber(selectedDecimal);

                Map<String, String> updatedButtons = TelegramBotContent.BUTTONS3.entrySet().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                e -> {
                                    String label = e.getKey().replace("✅ ", "");
                                    if (label.equals(String.valueOf(selectedDecimal))) {
                                        return "✅ " + label;
                                    } else {
                                        return label;
                                    }
                                },
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                java.util.LinkedHashMap::new
                        ));

                editInlineKeyboard(chatID, update.getCallbackQuery().getMessage().getMessageId(), updatedButtons);
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


    @Override
    public void deleteMessage(Long chatID, Map<Long, Integer> lastMessageIds) {
        //not released yet
    }

    public int sendInfoMessage(Long chatID, String textMessage){
        return this.sendMessage(chatID, textMessage, BUTTONS1);
    }
    private void editInlineKeyboard(Long chatID, int messageId, Map<String, String> buttons) {
        EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();
        editMarkup.setChatId(chatID.toString());
        editMarkup.setMessageId(messageId);

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData(entry.getValue());
            keyboard.add(List.of(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        editMarkup.setReplyMarkup(markup);

        try {
            execute(editMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
