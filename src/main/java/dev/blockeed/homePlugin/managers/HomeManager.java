package dev.blockeed.homePlugin.managers;

import dev.blockeed.homePlugin.HomePlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/*
 * Config layout:
 * homes:
 *   <uuid>:
 *     default: <name>
 *     homes:
 *       <name>: {world, x, y, z, yaw, pitch}
 *
 * The legacy layout (homes.<uuid>.{world, x, y, z, yaw, pitch}) is migrated
 * on startup into a home called "home", which becomes the default home.
 */
public class HomeManager {

    public static final int MAX_HOMES = 3;
    public static final String DEFAULT_HOME_NAME = "home";

    private static final Pattern NAME_PATTERN = Pattern.compile("[a-z0-9_-]{1,16}");

    private static Map<UUID, Map<String, Location>> homes = new HashMap<>();
    private static Map<UUID, String> defaultHomes = new HashMap<>();

    public static void init() {
        if (!HomePlugin.getInstance().getConfig().isConfigurationSection("homes")) return;
        ConfigurationSection homesSection = HomePlugin.getInstance().getConfig().getConfigurationSection("homes");
        migrateLegacyHomes(homesSection);
        for (String uuidString : homesSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                ConfigurationSection playerSection = homesSection.getConfigurationSection(uuidString);
                ConfigurationSection playerHomesSection = playerSection.getConfigurationSection("homes");
                if (playerHomesSection == null) continue;
                Map<String, Location> playerHomes = new LinkedHashMap<>();
                for (String name : playerHomesSection.getKeys(false)) {
                    Location location = readLocation(playerHomesSection.getConfigurationSection(name));
                    if (location == null) continue;
                    playerHomes.put(name, location);
                }
                homes.put(uuid, playerHomes);
                String defaultHome = playerSection.getString("default");
                if (defaultHome != null) defaultHomes.put(uuid, defaultHome);
            } catch (Exception e) {
                System.out.println("Invalid UUID found in config: " + uuidString);
            }
        }
    }

    private static void migrateLegacyHomes(ConfigurationSection homesSection) {
        boolean migrated = false;
        for (String uuidString : homesSection.getKeys(false)) {
            ConfigurationSection playerSection = homesSection.getConfigurationSection(uuidString);
            if (playerSection == null || !playerSection.contains("world")) continue;
            Map<String, Object> legacyLocation = playerSection.getValues(false);
            ConfigurationSection newPlayerSection = homesSection.createSection(uuidString);
            newPlayerSection.set("default", DEFAULT_HOME_NAME);
            newPlayerSection.createSection("homes").createSection(DEFAULT_HOME_NAME, legacyLocation);
            migrated = true;
        }
        if (migrated) HomePlugin.getInstance().saveConfig();
    }

    private static Location readLocation(ConfigurationSection locationSection) {
        if (locationSection == null) return null;
        String worldName = locationSection.getString("world");
        if (worldName == null) return null;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = locationSection.getDouble("x");
        double y = locationSection.getDouble("y");
        double z = locationSection.getDouble("z");
        float yaw = (float) locationSection.getDouble("yaw");
        float pitch = (float) locationSection.getDouble("pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static boolean isValidName(String name) {
        return NAME_PATTERN.matcher(name).matches();
    }

    public static Set<String> getHomeNames(Player player) {
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        if (playerHomes == null) return Collections.emptySet();
        return Collections.unmodifiableSet(playerHomes.keySet());
    }

    public static String getDefaultHomeName(Player player) {
        return defaultHomes.get(player.getUniqueId());
    }

    public static Location getHome(Player player, String name) {
        Map<String, Location> playerHomes = homes.get(player.getUniqueId());
        if (playerHomes == null) return null;
        return playerHomes.get(name);
    }

    public static Location getDefaultHome(Player player) {
        String defaultHome = getDefaultHomeName(player);
        if (defaultHome == null) return null;
        return getHome(player, defaultHome);
    }

    /**
     * @return false if the player already has {@link #MAX_HOMES} homes and {@code name} is a new one
     */
    public static boolean setHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Map<String, Location> playerHomes = homes.computeIfAbsent(uuid, k -> new LinkedHashMap<>());
        if (!playerHomes.containsKey(name) && playerHomes.size() >= MAX_HOMES) return false;

        ConfigurationSection playerSection = getPlayerSection(player);
        ConfigurationSection playerHomesSection = playerSection.isConfigurationSection("homes")
                ? playerSection.getConfigurationSection("homes")
                : playerSection.createSection("homes");
        ConfigurationSection locationSection = playerHomesSection.createSection(name);
        Location location = player.getLocation();
        locationSection.set("x", location.getX());
        locationSection.set("y", location.getY());
        locationSection.set("z", location.getZ());
        locationSection.set("yaw", location.getYaw());
        locationSection.set("pitch", location.getPitch());
        locationSection.set("world", location.getWorld().getName());
        playerHomes.put(name, location);

        if (!defaultHomes.containsKey(uuid)) {
            playerSection.set("default", name);
            defaultHomes.put(uuid, name);
        }
        HomePlugin.getInstance().saveConfig();
        return true;
    }

    /**
     * @return false if the player has no home called {@code name}
     */
    public static boolean setDefaultHome(Player player, String name) {
        if (getHome(player, name) == null) return false;
        getPlayerSection(player).set("default", name);
        defaultHomes.put(player.getUniqueId(), name);
        HomePlugin.getInstance().saveConfig();
        return true;
    }

    /**
     * If the deleted home was the default, the next remaining home becomes the default.
     *
     * @return false if the player has no home called {@code name}
     */
    public static boolean deleteHome(Player player, String name) {
        UUID uuid = player.getUniqueId();
        Map<String, Location> playerHomes = homes.get(uuid);
        if (playerHomes == null || playerHomes.remove(name) == null) return false;

        ConfigurationSection playerSection = getPlayerSection(player);
        playerSection.set("homes." + name, null);
        if (name.equals(defaultHomes.get(uuid))) {
            String newDefault = playerHomes.isEmpty() ? null : playerHomes.keySet().iterator().next();
            playerSection.set("default", newDefault);
            if (newDefault == null) defaultHomes.remove(uuid);
            else defaultHomes.put(uuid, newDefault);
        }
        HomePlugin.getInstance().saveConfig();
        return true;
    }

    private static ConfigurationSection getPlayerSection(Player player) {
        if (!HomePlugin.getInstance().getConfig().isConfigurationSection("homes")) {
            HomePlugin.getInstance().getConfig().createSection("homes");
        }
        ConfigurationSection homesSection = HomePlugin.getInstance().getConfig().getConfigurationSection("homes");
        String uuidString = player.getUniqueId().toString();
        if (!homesSection.isConfigurationSection(uuidString)) return homesSection.createSection(uuidString);
        return homesSection.getConfigurationSection(uuidString);
    }

}
