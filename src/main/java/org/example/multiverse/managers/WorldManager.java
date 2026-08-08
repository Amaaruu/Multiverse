package org.example.multiverse.managers;

import org.example.multiverse.Multiverse;
import org.example.multiverse.generators.VoidChunkGenerator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import java.io.File;

public class WorldManager {
    private final Multiverse plugin;
    private final ConfigManager configManager;

    public WorldManager(Multiverse plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    public void createWorld(String name, String type) {
        WorldCreator creator = new WorldCreator(name);

        if (type.equalsIgnoreCase("VOID")) {
            creator.generator(new VoidChunkGenerator());
            creator.generateStructures(false);
        } else if (type.equalsIgnoreCase("FLAT")) {
            creator.type(WorldType.FLAT);
        } else {
            creator.type(WorldType.NORMAL);
        }

        World world = creator.createWorld();

        if (type.equalsIgnoreCase("VOID")) {
            // Reglas de juego para Lobbies
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setStorm(false);

            // Crear el bloque inicial
            int bx = configManager.getConfig().getInt("void.x", 0);
            int by = configManager.getConfig().getInt("void.y", 64);
            int bz = configManager.getConfig().getInt("void.z", 0);
            String matName = configManager.getConfig().getString("void.block", "STONE");
            Material mat = Material.matchMaterial(matName);
            if (mat == null) mat = Material.STONE;

            world.getBlockAt(bx, by, bz).setType(mat);

            // Configurar spawn
            Location spawn = new Location(world, bx + 0.5, by + 1.0, bz + 0.5, 0f, 0f);
            world.setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());

            // Guardar en config
            saveWorldToConfig(name, "VOID", spawn);
        } else {
            saveWorldToConfig(name, type.toUpperCase(), world.getSpawnLocation());
        }
    }

    public void loadAutoWorlds() {
        if (!configManager.getConfig().contains("worlds")) return;
        for (String worldName : configManager.getConfig().getConfigurationSection("worlds").getKeys(false)) {
            boolean autoLoad = configManager.getConfig().getBoolean("worlds." + worldName + ".auto-load", true);
            if (autoLoad) {
                String type = configManager.getConfig().getString("worlds." + worldName + ".type", "NORMAL");
                WorldCreator creator = new WorldCreator(worldName);
                if (type.equalsIgnoreCase("VOID")) {
                    creator.generator(new VoidChunkGenerator());
                }
                Bukkit.createWorld(creator);
                plugin.getLogger().info("Mundo cargado: " + worldName);
            }
        }
    }

    private void saveWorldToConfig(String name, String type, Location spawn) {
        String path = "worlds." + name;
        configManager.getConfig().set(path + ".type", type);
        configManager.getConfig().set(path + ".auto-load", true);
        configManager.getConfig().set(path + ".spawn.x", spawn.getX());
        configManager.getConfig().set(path + ".spawn.y", spawn.getY());
        configManager.getConfig().set(path + ".spawn.z", spawn.getZ());
        configManager.getConfig().set(path + ".spawn.yaw", spawn.getYaw());
        configManager.getConfig().set(path + ".spawn.pitch", spawn.getPitch());
        configManager.saveConfig();
    }

    public Location getSpawn(String name) {
        String path = "worlds." + name + ".spawn";
        if (configManager.getConfig().contains(path)) {
            World w = Bukkit.getWorld(name);
            if (w == null) return null;
            double x = configManager.getConfig().getDouble(path + ".x");
            double y = configManager.getConfig().getDouble(path + ".y");
            double z = configManager.getConfig().getDouble(path + ".z");
            float yaw = (float) configManager.getConfig().getDouble(path + ".yaw");
            float pitch = (float) configManager.getConfig().getDouble(path + ".pitch");
            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }
}