package dev.blockeed.homePlugin.listeners;

import dev.blockeed.homePlugin.managers.CombatManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        CombatManager.tag(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        CombatManager.untag(event.getEntity());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CombatManager.untag(event.getPlayer());
    }

}
