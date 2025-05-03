package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.util.UUID;

public class ChatListener implements Listener {

    private final perfectUnityPlugin plugin;

    public ChatListener(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!plugin.getRenameQueue().containsKey(uuid)) return;

        e.setCancelled(true);

        String oldName = plugin.getRenameQueue().remove(uuid);
        String newName = e.getMessage().trim().toLowerCase();

        if (newName.isEmpty() || newName.contains(" ")) {
            player.sendMessage(ChatColor.RED + "Invalid home name. No spaces allowed.");
            return;
        }


        YamlConfiguration data = plugin.getHomeData(player);

        if (!data.contains(oldName)) {
            player.sendMessage(ChatColor.RED + "Home no longer exists.");
            return;
        }

        if (data.contains(newName)) {
            player.sendMessage(ChatColor.RED + "A home with that name already exists.");
            return;
        }

        data.set(newName, data.get(oldName));
        data.set(oldName, null);

        try {
            data.save(plugin.getHomeFile(player));
            player.sendMessage(ChatColor.GREEN + "Home renamed to '" + newName + "'");
        } catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong saving the rename.");
        }
    }
}
