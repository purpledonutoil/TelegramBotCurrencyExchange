import executor.TelegramService;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import scheduler.SchedulerService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppTelegramBot {

    public static void main(String[] args) throws TelegramApiException {

        String configPath = System.getProperty("config.path", "./config.properties");
        if (configPath == null || configPath.isEmpty()) {
            configPath = "./config.properties";
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String botToken = props.getProperty("bot.token");
        String botName = props.getProperty("bot.name");

        TelegramService telegramService = new TelegramService(botToken, botName);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramService);

        //Start SchedulerService
        SchedulerService schedulerService = new SchedulerService(telegramService::sendInfoMessage);
        schedulerService.start();
    }
}
