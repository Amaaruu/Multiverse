package org.example.multiverse;

import org.example.multiverse.commands.MultiverseCommand;
import org.example.multiverse.managers.ConfigManager;
import org.example.multiverse.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Multiverse extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        // Inicializar Managers
        this.configManager = new ConfigManager(this);
        this.worldManager = new WorldManager(this, configManager);

        // Registrar Comandos y Eventos
        getCommand("mv").setExecutor(new MultiverseCommand(this, worldManager, configManager));
        getServer().getPluginManager().registerEvents(this, this);

        // Cargar mundos autoload
        worldManager.loadAutoWorlds();

        getLogger().info("Multiverse 1.0.0 habilitado correctamente.");
    }

    // Proteccion contra caidas en el vacio
    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (p.getLocation().getY() < 0) {
            String worldName = p.getWorld().getName();
            if (configManager.getConfig().contains("worlds." + worldName + ".type")) {
                String type = configManager.getConfig().getString("worlds." + worldName + ".type");
                if (type.equalsIgnoreCase("VOID")) {
                    Location spawn = worldManager.getSpawn(worldName);
                    if (spawn != null) {
                        p.teleport(spawn);
                    }
                }
            }
        }
    }
}