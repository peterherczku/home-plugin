package dev.blockeed.homePlugin.commands;

import dev.blockeed.homePlugin.managers.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SethomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player player)) {
            return true;
        }
        HomeManager.setHome(player);
        player.sendMessage("Successfully set your new home :)");
        return false;
    }
}
