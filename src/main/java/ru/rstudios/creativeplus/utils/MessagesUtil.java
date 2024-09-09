package ru.rstudios.creativeplus.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.List;

public class MessagesUtil {

    public static String getMessage (String messagePath) {
        FileConfiguration messages = FileUtil.loadConfiguration("messages.yml");
        return messages.getString(messagePath, "§cСообщение " + messagePath + " не найдено");
    }

    public static List<String> getMessageList (String messagePath) {
        FileConfiguration messages = FileUtil.loadConfiguration("messages.yml");
        return messages.getStringList(messagePath).isEmpty() ? Collections.singletonList("§cСписок сообщений " + messagePath + " не найден по заданному пути") : messages.getStringList(messagePath);
    }

}
