package schedulesender;

import java.time.LocalTime;

public interface ScheduledMessageSender {
    void scheduleDailyMessage();
    long calculateDelay(LocalTime targetTime);
}
