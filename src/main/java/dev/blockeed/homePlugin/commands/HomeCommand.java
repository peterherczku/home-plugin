package dev.blockeed.homePlugin.commands;

import dev.blockeed.homePlugin.managers.HomeManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        Location location = HomeManager.getHome(player);
        if (location == null) {
            player.sendMessage("You do not have a home yet");
            return true;
        }
        player.teleport(location);
        player.sendMessage("Teleported you home :)");
        return false;
    }
}
