package org.example;

import org.example.settings.Settings;

import static org.example.settings.MessageSettings.modifyMessageSettings;
import static org.example.settings.MessageSettings.readMessageSettings;

public class Main {
    public static void main(String[] args) {
        System.out.println("Читаем настройки:");
        Settings settings = readMessageSettings();
        System.out.println(settings);

        System.out.println("Изменяем время уведомления:");
        modifyMessageSettings("notificationTime", 12);

        System.out.println("Читаем обновленные настройки:");
        Settings updatedSettings = readMessageSettings();
        System.out.println(updatedSettings);
    }

}