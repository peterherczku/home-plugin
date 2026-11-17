package dev.blockeed.homePlugin;

import dev.blockeed.homePlugin.commands.DelhomeCommand;
import dev.blockeed.homePlugin.commands.HomeCommand;
import dev.blockeed.homePlugin.commands.SetdefaulthomeCommand;
import dev.blockeed.homePlugin.commands.SethomeCommand;
import dev.blockeed.homePlugin.listeners.CombatListener;
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
        getCommand("setdefaulthome").setExecutor(new SetdefaulthomeCommand());
        getCommand("delhome").setExecutor(new DelhomeCommand());
        getServer().getPluginManager().registerEvents(new CombatListener(), this);
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
