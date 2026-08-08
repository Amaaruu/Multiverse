package org.example.multiverse.commands;

import org.example.multiverse.Multiverse;
import org.example.multiverse.managers.ConfigManager;
import org.example.multiverse.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MultiverseCommand implements CommandExecutor {
    private final Multiverse plugin;
    private final WorldManager worldManager;
    private final ConfigManager config;

    public MultiverseCommand(Multiverse plugin, WorldManager worldManager, ConfigManager config) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.config = config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("multiverse.admin")) {
            sender.sendMessage(config.getMessage("no-permission"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage("§b--- Multiverse ---");
            sender.sendMessage("§e/mv create <nombre> <normal/flat/void>");
            sender.sendMessage("§e/mv tp <mundo>");
            sender.sendMessage("§e/mv setspawn");
            return true;
        }

        if (args[0].equalsIgnoreCase("create") && args.length == 3) {
            String name = args[1];
            String type = args[2].toUpperCase();
            sender.sendMessage("§eCreando mundo " + name + " de tipo " + type + "...");
            worldManager.createWorld(name, type);
            sender.sendMessage(config.getMessage("world-created").replace("%world%", name));
            return true;
        }

        if (args[0].equalsIgnoreCase("tp") && args.length == 2 && sender instanceof Player) {
            Player p = (Player) sender;
            String name = args[1];
            World w = Bukkit.getWorld(name);
            if (w != null) {
                Location spawn = worldManager.getSpawn(name);
                p.teleport(spawn != null ? spawn : w.getSpawnLocation());
                p.sendMessage(config.getMessage("teleported").replace("%world%", name));
            } else {
                p.sendMessage(config.getMessage("world-not-found").replace("%world%", name));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("setspawn") && sender instanceof Player) {
            Player p = (Player) sender;
            worldManager.setSpawn(p.getWorld().getName(), p.getLocation());
            p.sendMessage(config.getMessage("spawn-set"));
            return true;
        }

        return true;
    }
}