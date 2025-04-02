package executor;

import io.github.cdimascio.dotenv.Dotenv;

public class TelegramService {
    private static String botToken;
    private static String botName;

    TelegramService(){
        Dotenv dotenv = Dotenv.load();
        botToken = dotenv.get("BOT_TOKEN");
        botName = dotenv.get("BOT_NAME");
    }

    public static String getBotName() {
        return botName;
    }

    public static String getBotToken() {
        return botToken;
    }
}
