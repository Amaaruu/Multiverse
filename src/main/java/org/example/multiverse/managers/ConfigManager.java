package org.example.multiverse.managers;

import org.example.multiverse.Multiverse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final Multiverse plugin;

    public ConfigManager(Multiverse plugin) {
        this.plugin = plugin;
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }

    public String getMessage(String key) {
        String prefix = getConfig().getString("messages.prefix", "");
        String msg = getConfig().getString("messages." + key, "");
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }
}