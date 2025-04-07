package storage;

import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Storage {

    private static final Storage INSTANCE = new Storage();

    private final Map<Long, UserSettings> data = new ConcurrentHashMap<>();

    private Storage() {
    }

    public static Storage getInstance() {
        return INSTANCE;
    }

    public void saveUserSettings(Long chatId, UserSettings settings) {
        if (settings == null) return;
        data.put(chatId, settings);
    }

    public UserSettings getUserSettings(Long chatId) {
        return data.computeIfAbsent(chatId, k -> new UserSettings());
    }

    // Emulation query to database
    public Map<Long, UserSettings> getFilteredByNotificationTimeData(int notificationHour){
        Map<Long, UserSettings> result = new ConcurrentHashMap<>();

        Gson gsonBuilder = new Gson();
        for (Map.Entry<Long, UserSettings> entry: data.entrySet()){
            UserSettings currentSettings = entry.getValue();
            if (currentSettings.getNotificationTime() == notificationHour ) {
                // create a copy of original User Settings
                String json = gsonBuilder.toJson(currentSettings);
                result.put(entry.getKey(), gsonBuilder.fromJson(json, UserSettings.class));
            }
        }
        return result;
    }
}
