package dev.blockeed.homePlugin.managers;

import dev.blockeed.homePlugin.HomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {

    private static Map<UUID, Location> homes = new HashMap<>();

    public static void init() {
        if (!HomePlugin.getInstance().getConfig().isConfigurationSection("homes")) return;
        ConfigurationSection homesSection = HomePlugin.getInstance().getConfig().getConfigurationSection("homes");
        for (String uuidString : homesSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                ConfigurationSection locationSection = homesSection.getConfigurationSection(uuidString);
                String worldName = locationSection.getString("world");
                if (Bukkit.getWorld(worldName) == null) {
                    continue;
                }
                World world = Bukkit.getWorld(worldName);
                double x = locationSection.getDouble("x");
                double y = locationSection.getDouble("y");
                double z = locationSection.getDouble("z");
                float yaw = (float) locationSection.getDouble("yaw");
                float pitch = (float) locationSection.getDouble("pitch");
                Location location = new Location(world, x, y, z, yaw, pitch);
                homes.put(uuid, location);
            } catch (Exception e) {
                System.out.println("Invalid UUID found in config: " + uuidString);
            }
        }
    }

    public static Location getHome(Player player) {
        return homes.get(player.getUniqueId());
    }

    public static void setHome(Player player) {
        if (!HomePlugin.getInstance().getConfig().isConfigurationSection("homes")) {
            HomePlugin.getInstance().getConfig().createSection("homes");
        }
        ConfigurationSection homesSection = HomePlugin.getInstance().getConfig().getConfigurationSection("homes");
        if (!homesSection.isConfigurationSection(player.getUniqueId().toString())) homesSection.createSection(player.getUniqueId().toString());
        ConfigurationSection locationSection = homesSection.createSection(player.getUniqueId().toString());
        Location location = player.getLocation();
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());
        locationSection.set("yaw", location.getYaw());
        locationSection.set("pitch", location.getPitch());
        locationSection.set("world", location.getWorld().getName());
        HomePlugin.getInstance().saveConfig();
        homes.put(player.getUniqueId(), location);
    }

}
