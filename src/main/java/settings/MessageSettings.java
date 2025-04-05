package settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MessageSettings {
    private static final String FILE_PATH = "settings/settings.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    // Метод для чтения настроек
    public static Settings readMessageSettings() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            return gson.fromJson(reader, Settings.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    // Метод для изменения настроек
    public static void modifyMessageSettings(String key, Object value) {
        Settings settings = readMessageSettings();
        if (settings != null) {
            switch (key) {
                case "bank":
                    settings.setBank((String) value);
                    break;
                case "currency1":
                    settings.setCurrency1((String) value);
                    break;
                case "currency2":
                    settings.setCurrency2((String) value);
                    break;
                case "decimalPlaces":
                    settings.setDecimalPlaces((int) value);
                    break;
                case "notificationTime":
                    settings.setNotificationTime((int) value);
                    break;
                default:
                    System.out.println("Ошибка: неизвестный ключ");
                    return;
            }
            saveSettings(settings);
        }
    }

    //  Для сохранения настроек обратно в файл
    private static void saveSettings(Settings settings) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(settings, writer);
            System.out.println("Настройки успешно обновлены!");
        } catch (IOException e) {     // ОШИБКА ПРИ ЧТЕНИИ ФАЙЛОВ
            e.printStackTrace();
        }
    }
}
