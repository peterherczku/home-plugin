package dev.blockeed.homePlugin;

import dev.blockeed.homePlugin.commands.HomeCommand;
import dev.blockeed.homePlugin.commands.SethomeCommand;
import dev.blockeed.homePlugin.managers.HomeManager;
import org.bukkit.plugin.java.JavaPlugin;

public class HomePlugin extends JavaPlugin {

    private static HomePlugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        initConfig();
        HomeManager.init();
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("sethome").setExecutor(new SethomeCommand());
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void initConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
    }

    public static HomePlugin getInstance() {
        return instance;
    }
}
