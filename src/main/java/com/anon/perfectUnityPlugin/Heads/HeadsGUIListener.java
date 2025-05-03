package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class HeadsGUIListener implements Listener {

    private final perfectUnityPlugin plugin;
    public HeadsGUIListener(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onGUIClick(InventoryClickEvent e) {

    }
}
