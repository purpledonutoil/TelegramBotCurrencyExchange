package storage;

import java.util.HashMap;
import java.util.Map;

public class Storage {

    private static final Storage INSTANCE = new Storage();

    private final Map<Long, UserSettings> data = new HashMap<>();

    private Storage() {
    }

    public static Storage getInstance() {
        return INSTANCE;
    }

    public void saveUserSettings(long chatId, UserSettings settings) {
        if (settings == null) return;
        data.put(chatId, settings);
    }

    public UserSettings getUserSettings(long chatId) {
        return data.computeIfAbsent(chatId, k -> new UserSettings());
    }
}
