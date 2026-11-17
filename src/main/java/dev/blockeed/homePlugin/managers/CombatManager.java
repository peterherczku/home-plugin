package dev.blockeed.homePlugin.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private static final long COMBAT_DURATION_MILLIS = 10_000;

    private static Map<UUID, Long> lastDamageTimes = new HashMap<>();

    public static void tag(Player player) {
        lastDamageTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static void untag(Player player) {
        lastDamageTimes.remove(player.getUniqueId());
    }

    public static long getRemainingMillis(Player player) {
        Long lastDamage = lastDamageTimes.get(player.getUniqueId());
        if (lastDamage == null) return 0;
        return Math.max(0, lastDamage + COMBAT_DURATION_MILLIS - System.currentTimeMillis());
    }

    public static boolean isInCombat(Player player) {
        return getRemainingMillis(player) > 0;
    }

}
