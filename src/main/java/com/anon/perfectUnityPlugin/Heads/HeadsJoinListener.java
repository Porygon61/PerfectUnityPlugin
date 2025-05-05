package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.Utility;
import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;

public class HeadsJoinListener implements Listener {
    private final perfectUnityPlugin plugin;

    public HeadsJoinListener(perfectUnityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        saveHead(player);
        createAuthorityFile(player);
    }

    public void saveHead(Player player) {
        YamlConfiguration data = plugin.getHeadsData();
        String name = player.getName().toLowerCase();
        data.set(name + ".type", "player");
        data.set(name + ".owner", player.getName());
        data.set(name + ".owner-uuid", player.getUniqueId().toString());
        data.set(name + ".texture", Utility.getInstance().getBase64FromURL(player));
        data.set(name + ".display-color", "§6");
        data.set(name + ".display", player.getName());

        try {
            data.save(plugin.getHeadsFile());
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void createAuthorityFile(Player player) {
        YamlConfiguration data = plugin.getHeadsPlayerData(player);
        //TODO file creation
    }

}
