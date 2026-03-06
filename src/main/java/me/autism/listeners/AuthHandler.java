package me.autism.listeners;

import me.autism.Tantrum;
import me.autism.managers.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.mindrot.jbcrypt.BCrypt;
import java.util.*;

public class AuthHandler implements Listener, CommandExecutor {
    private final DataManager data;
    private final Set<UUID> locked = new HashSet<>();

    public AuthHandler(DataManager data) { this.data = data; }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        locked.add(e.getPlayer().getUniqueId());
        e.getPlayer().setOp(false); // Disable OP on join
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (label.equalsIgnoreCase("register")) {
            if (data.getHash(p.getName()) != null) { p.sendMessage("Already registered."); return true; }
            data.save(p.getName(), BCrypt.hashpw(args[0], BCrypt.gensalt(12)));
            locked.remove(p.getUniqueId());
            p.sendMessage("Registered successfully.");
        } 
        
        else if (label.equalsIgnoreCase("login")) {
            String hash = data.getHash(p.getName());
            if (hash != null && BCrypt.checkpw(args[0], hash)) {
                locked.remove(p.getUniqueId());
                p.setOp(true); // Restore OP
                p.sendMessage("Logged in.");
            } else { p.sendMessage("Invalid password."); }
        }

        else if (label.equalsIgnoreCase("resetpass")) {
            // Logic: check old pass, then update with new hash
            String hash = data.getHash(p.getName());
            if (hash != null && BCrypt.checkpw(args[0], hash)) {
                data.save(p.getName(), BCrypt.hashpw(args[1], BCrypt.gensalt(12)));
                p.sendMessage("Password changed.");
            }
        }

        else if (label.equalsIgnoreCase("password") && args[0].equalsIgnoreCase("reset")) {
            if (!p.isOp()) { p.sendMessage("No permission."); return true; }
            data.save(args[1], BCrypt.hashpw(args[2], BCrypt.gensalt(12)));
            p.sendMessage("Reset password for " + args[1]);
        }
        return true;
    }
}