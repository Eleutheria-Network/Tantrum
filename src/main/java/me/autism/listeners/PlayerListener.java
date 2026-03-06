package me.autism.listeners;

import me.autism.Tantrum;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerListener implements Listener, CommandExecutor {
    private final Tantrum plugin;
    private final Set<UUID> locked = new HashSet<>();
    private final Set<UUID> wasOp = new HashSet<>();

    public PlayerListener(Tantrum plugin) { this.plugin = plugin; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        locked.add(p.getUniqueId());
        if (p.isOp()) { wasOp.add(p.getUniqueId()); p.setOp(false); }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (locked.contains(e.getPlayer().getUniqueId())) e.setTo(e.getFrom());
    }

    @EventHandler
    public void onCmd(PlayerCommandPreprocessEvent e) {
        if (locked.contains(e.getPlayer().getUniqueId()) && !e.getMessage().startsWith("/log") && !e.getMessage().startsWith("/reg")) {
            e.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        File f = new File(plugin.getDataFolder(), "data.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);

        if (cmd.getName().equalsIgnoreCase("register")) {
            if (config.contains(p.getUniqueId().toString())) return true;
            config.set(p.getUniqueId().toString(), args[0]);
            try { config.save(f); } catch (IOException e) { e.printStackTrace(); }
            locked.remove(p.getUniqueId());
            p.sendMessage("Registered");
        } else if (cmd.getName().equalsIgnoreCase("login")) {
            if (args[0].equals(config.getString(p.getUniqueId().toString()))) {
                locked.remove(p.getUniqueId());
                if (wasOp.contains(p.getUniqueId())) p.setOp(true);
                p.sendMessage("Logged in");
            }
        }
        return true;
    }
}