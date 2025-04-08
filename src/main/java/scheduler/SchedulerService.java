package scheduler;

import storage.Storage;
import storage.UserSettings;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class SchedulerService {
    private static final int MIN_HOUR = 9;
    private static final int MAX_HOUR = 18;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final BiFunction<Long, String, Integer> notificationAction;
    private boolean isRunning = false;

    public SchedulerService(BiFunction<Long, String, Integer> action) {
        this.notificationAction = action;
    }

    public void start() {
        if (isRunning) {
            return;
        }

        // Calculate time to start next hour
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);
        long initialDelay = ChronoUnit.MILLIS.between(now, nextHour);

        // Start scheduler
        scheduler.scheduleAtFixedRate(
                this::processNotifications,
                initialDelay,
                TimeUnit.HOURS.toMillis(1),
                TimeUnit.MILLISECONDS
        );

        isRunning = true;
    }

    public void processNow() {
        scheduler.execute(this::processNotifications);
    }

    private void processNotifications() {
        int currentHour = LocalDateTime.now().getHour();
        if (currentHour < MIN_HOUR || currentHour > MAX_HOUR) {
            return;
        }

        Map<Long, UserSettings> batchToNotification = Storage.getInstance().getFilteredByNotificationTimeData(currentHour);

        if (!batchToNotification.isEmpty()) {
            BatchMessagesSender messageSender = new BatchMessagesSender(notificationAction);
            messageSender.sendMessages(batchToNotification);
        }
    }

    public void stop() {
        if (!isRunning) {
            return;
        }

        scheduler.shutdown();
        try {
            // Waiting for finishing current tasks
            if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
