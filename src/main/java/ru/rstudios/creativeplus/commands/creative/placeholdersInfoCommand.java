package ru.rstudios.creativeplus.commands.creative;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class placeholdersInfoCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        sender.sendMessage("§bCreative+ §8» §fДоступные плейсхолдеры:");
        sender.sendMessage("§bCreative+ §8» §f%player% §7- §fИгрок из события");
        sender.sendMessage("§bCreative+ §8» §f%default% §7- §fСущность, вызвавшая событие");
        sender.sendMessage("§bCreative+ §8» §f%player% §7- §fИгрок из события");
        sender.sendMessage("§bCreative+ §8» §f%victim% §7- §fЖертва из события");
        sender.sendMessage("§bCreative+ §8» §f%damager% §7- §fТот, кто нанёс урон в событии");
        sender.sendMessage("§bCreative+ §8» §f%killer% §7- §fУбийца из события");
        sender.sendMessage("§bCreative+ §8» §f%shooter% §7- §fСтрелок из события");
        sender.sendMessage("§bCreative+ §8» §f%entity% §7- §fСущность из события");
        return true;
    }
}
