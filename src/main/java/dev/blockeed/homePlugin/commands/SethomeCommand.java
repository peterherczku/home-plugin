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
        String name;
        if (args.length == 0) {
            // Without a name, overwrite the default home (like the old single-home /sethome)
            name = HomeManager.getDefaultHomeName(player);
            if (name == null) name = HomeManager.DEFAULT_HOME_NAME;
        } else {
            name = args[0].toLowerCase();
            if (!HomeManager.isValidName(name)) {
                player.sendMessage("Home names can only contain letters, numbers, _ and - (max 16 characters)");
                return true;
            }
        }
        if (!HomeManager.setHome(player, name)) {
            player.sendMessage("You can only have " + HomeManager.MAX_HOMES + " homes. Overwrite one of yours: " + String.join(", ", HomeManager.getHomeNames(player)));
            return true;
        }
        player.sendMessage("Successfully set your home " + name + " :)");
        return true;
    }
}
