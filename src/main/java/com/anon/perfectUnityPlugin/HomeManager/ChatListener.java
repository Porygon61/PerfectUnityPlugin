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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!perfectUnityPlugin.getInstance().getRenameQueue().containsKey(uuid)) return;

        e.setCancelled(true);

        String oldName = perfectUnityPlugin.getInstance().getRenameQueue().remove(uuid);
        String newName = e.getMessage().trim().toLowerCase();

        if (newName.isEmpty() || newName.contains(" ")) {
            player.sendMessage(ChatColor.RED + "Invalid home name. No spaces allowed.");
            return;
        }

        File file = new File(perfectUnityPlugin.getInstance().getDataFolder(), "homes/" + uuid + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

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
            data.save(file);
            player.sendMessage(ChatColor.GREEN + "Home renamed to '" + newName + "'");
        } catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong saving the rename.");
        }
    }
}
