package storage;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    private static final Storage instance = new Storage();
    
    private final Map<Long, UserSettings> data = new HashMap<>();

    private Storage() {}

    public static Storage getInstance() {
        return instance;
    }

    public UserSettings getUserSettings(Long chatId) {
        return data.getOrDefault(chatId, new UserSettings());
    }

    public void saveUserSettings(Long chatId, UserSettings settings) {
        data.put(chatId, settings);
    }
}
