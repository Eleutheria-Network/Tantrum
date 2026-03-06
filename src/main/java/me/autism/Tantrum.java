package me.autism;

import org.bukkit.plugin.java.JavaPlugin;
import me.autism.listeners.PlayerListener;
import java.io.File;

public class Tantrum extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();
        File file = new File(getDataFolder(), "data.yml");
        if (!file.exists()) saveResource("data.yml", false);

        PlayerListener listener = new PlayerListener(this);
        getServer().getPluginManager().registerEvents(listener, this);

        getCommand("register").setExecutor(listener);
        getCommand("login").setExecutor(listener);

        getLogger().info("Tantrum has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Tantrum has been disabled!");
    }
}