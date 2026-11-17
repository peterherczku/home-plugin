package dev.blockeed.homePlugin.commands;

import dev.blockeed.homePlugin.managers.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class DelhomeCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("Usage: /delhome <name>");
            return true;
        }
        String name = args[0].toLowerCase();
        if (!HomeManager.deleteHome(player, name)) {
            player.sendMessage("You do not have a home called " + name);
            return true;
        }
        player.sendMessage("Deleted your home " + name);
        String defaultHome = HomeManager.getDefaultHomeName(player);
        if (defaultHome != null) {
            player.sendMessage("Your default home is " + defaultHome);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player) || args.length != 1) return Collections.emptyList();
        String prefix = args[0].toLowerCase();
        return HomeManager.getHomeNames(player).stream().filter(name -> name.startsWith(prefix)).toList();
    }
}
