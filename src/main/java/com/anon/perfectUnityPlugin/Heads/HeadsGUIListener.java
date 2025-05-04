package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class HeadsGUIListener implements Listener {

    private final perfectUnityPlugin plugin;

    public HeadsGUIListener(perfectUnityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!(ChatColor.stripColor(e.getView().getTitle()).startsWith("All Heads"))) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        if (clicked.getType() == Material.ARROW) {
            String title = e.getView().getTitle();
            String[] parts = title.split(" ");
            int current = Integer.parseInt(parts[3].split("/")[0]) - 1;
            int newPage = clicked.getItemMeta().getDisplayName().contains("Previous") ? current - 1 : current + 1;
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new HeadsCommand(plugin).openHeadsGUI(player, plugin.getHeadsGUIs().get(player.getUniqueId()), newPage));
            return;
        }

        String headName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        YamlConfiguration data = plugin.getHeadsGUIs().get(player.getUniqueId());

        if (data == null || !data.contains(headName)) return;

        switch (e.getClick()) {
            case LEFT:
                GiveHead.giveSingleHead(player, headName);
                player.closeInventory();
                break;

            case RIGHT:
                GiveHead.openAmountGUI(player, headName);

            default:
                break;
        }
    }
}
