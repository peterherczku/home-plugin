package dev.blockeed.homePlugin.commands;

import dev.blockeed.homePlugin.managers.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SetdefaulthomeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("Usage: /setdefaulthome <name>");
            return true;
        }
        String name = args[0].toLowerCase();
        if (!HomeManager.setDefaultHome(player, name)) {
            player.sendMessage("You do not have a home called " + name);
            return true;
        }
        player.sendMessage("Your default home is now " + name);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player) || args.length != 1) return Collections.emptyList();
        String prefix = args[0].toLowerCase();
        return HomeManager.getHomeNames(player).stream().filter(name -> name.startsWith(prefix)).toList();
    }
}
