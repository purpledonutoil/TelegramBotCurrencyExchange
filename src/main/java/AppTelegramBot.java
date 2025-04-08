import executor.TelegramService;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import scheduler.SchedulerService;

public class AppTelegramBot {

    public static void main(String[] args) throws TelegramApiException {
        Dotenv dotenv = Dotenv.load();
        String botToken = dotenv.get("BOT_TOKEN");
        String botName = dotenv.get("BOT_NAME");

        TelegramService telegramService = new TelegramService(botToken, botName);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramService);

        //Start SchedulerService
        SchedulerService schedulerService = new SchedulerService(telegramService::sendInfoMessage);
        schedulerService.start();
    }
}
