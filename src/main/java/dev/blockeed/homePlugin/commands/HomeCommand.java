package dev.blockeed.homePlugin.commands;

import dev.blockeed.homePlugin.managers.CombatManager;
import dev.blockeed.homePlugin.managers.HomeManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class HomeCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        if (CombatManager.isInCombat(player)) {
            long seconds = (long) Math.ceil(CombatManager.getRemainingMillis(player) / 1000.0);
            player.sendMessage("You cannot use /home while in combat! Try again in " + seconds + " seconds.");
            return true;
        }
        Location location;
        if (args.length == 0) {
            location = HomeManager.getDefaultHome(player);
            if (location == null) {
                player.sendMessage("You do not have a home yet");
                return true;
            }
        } else {
            String name = args[0].toLowerCase();
            location = HomeManager.getHome(player, name);
            if (location == null) {
                player.sendMessage("You do not have a home called " + name + ". Your homes: " + String.join(", ", HomeManager.getHomeNames(player)));
                return true;
            }
        }
        player.teleport(location);
        player.sendMessage("Teleported you home :)");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player) || args.length != 1) return Collections.emptyList();
        String prefix = args[0].toLowerCase();
        return HomeManager.getHomeNames(player).stream().filter(name -> name.startsWith(prefix)).toList();
    }
}
