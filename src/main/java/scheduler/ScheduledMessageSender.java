package scheduler;

import java.time.LocalTime;

public interface ScheduledMessageSender {
    void scheduleDailyMessage();
    long calculateDelay(LocalTime targetTime);
}
