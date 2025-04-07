package scheduler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

import banking.Bank;
import banking.BankService;
import banking.CurrencyRate;
import storage.UserSettings;
import utils.InfoMessage;

public class BatchMessagesSender {
    private static final int MAX_RETRY_COUNT = 1;
    private static final int RETRY_DELAY_MINUTES = 5;
    private static final int CHECK_COMPLETION_INTERVAL_MINUTES = 1;

    private final AtomicInteger pendingTasksCount = new AtomicInteger(0);
    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private final BiFunction<Long, String, Integer> notificationAction;
    private final ScheduledExecutorService scheduler;

    public BatchMessagesSender(BiFunction<Long, String, Integer> notificationAction) {
        this(notificationAction, Runtime.getRuntime().availableProcessors());
    }

    public BatchMessagesSender(BiFunction<Long, String, Integer> notificationAction, int threads) {
        this.notificationAction = notificationAction;
        this.scheduler = Executors.newScheduledThreadPool(threads);
    }

    public void sendMessages(Map<Long, UserSettings> messagesSettings) {

        pendingTasksCount.addAndGet(messagesSettings.size());

        for (Map.Entry<Long, UserSettings> userSettings : messagesSettings.entrySet()) {
            Map<Bank, List<CurrencyRate>> bankRates = BankService.getBankRates(userSettings.getValue());
            String prettyTextMessage = InfoMessage.getPrettyMessage(bankRates, userSettings.getValue().getRoundNumber());

            scheduler.submit(() -> sendMessageWithRetry(userSettings.getKey(), prettyTextMessage, 0));
        }

        startCompletionChecker();
    }

    private void sendMessageWithRetry(Long chatId, String textMessage, int retryCount) {
        // if shutdown initiated then don't do anything
        if (shutdownInitiated.get()) {
            return;
        }

        int sendingResult = notificationAction.apply(chatId, textMessage);

        // success case
        if (sendingResult != 0) {
            pendingTasksCount.decrementAndGet();
            return;
        }

        int nextRetryCount = retryCount + 1;
        if (nextRetryCount <= MAX_RETRY_COUNT) {
            scheduler.schedule(
                    () -> sendMessageWithRetry(chatId, textMessage, nextRetryCount),
                    RETRY_DELAY_MINUTES,
                    TimeUnit.MINUTES
            );
        } else {
            // If we use all retry then cancelling task
            pendingTasksCount.decrementAndGet();
        }
    }

    private void startCompletionChecker() {
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (pendingTasksCount.get() == 0 && !shutdownInitiated.get()) {
                        shutdown();
                    }
                },
                CHECK_COMPLETION_INTERVAL_MINUTES,
                CHECK_COMPLETION_INTERVAL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    public void shutdown() {
        if (shutdownInitiated.get()) {
            return;
        }

        shutdownInitiated.getAndSet(true);
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
