package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class GUIListener implements Listener {

    private final perfectUnityPlugin plugin;

    public GUIListener(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().startsWith("Your Homes")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!clicked.hasItemMeta()) return;

        if (clicked.getType() == Material.ARROW) {
            String title = e.getView().getTitle();
            String[] parts = title.split(" ");
            int current = Integer.parseInt(parts[3].split("/")[0]) - 1;
            int newPage = clicked.getItemMeta().getDisplayName().contains("Previous") ? current - 1 : current + 1;
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    new HomeCommand(plugin).openHomeGUI(player, plugin.getHomeGUIs().get(player.getUniqueId()), newPage));
            return;
        }

        String homeName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        YamlConfiguration data = plugin.getHomeGUIs().get(player.getUniqueId());

        // Make sure the clicked name exists as a home
        if (data == null || !data.contains(homeName)) return;


        switch (e.getClick()) {
            case LEFT:
                // Teleport
                player.performCommand("home " + homeName);
                player.closeInventory();
                break;

            case RIGHT:
                HomeEditGUI.open(player, homeName);
                break;


            default:
                break;
        }
    }


    @EventHandler
    public void onEditClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String title = e.getView().getTitle();
        if (!title.startsWith("Edit Home:")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!clicked.hasItemMeta()) return;

        String homeName = plugin.getEditSessions().get(player.getUniqueId());
        if (homeName == null) return;


        YamlConfiguration data = plugin.getHomeData(player);

        String action = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        switch (action) {
            case "Icon":
                player.closeInventory();
                IconEditGUI.openIconEditGUI(player, 0);
                break;

            case "Rename":
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "Type a new name in chat to rename home '" + homeName + "'");
                plugin.getRenameQueue().put(player.getUniqueId(), homeName);
                break;

            case "Delete":
                data.set(homeName, null);
                plugin.getEditSessions().remove(player.getUniqueId());
                try {
                    data.save(plugin.getHomeFile(player));
                    player.sendMessage(ChatColor.RED + "Deleted home '" + homeName + "'");
                } catch (Exception ex) {
                    player.sendMessage(ChatColor.DARK_RED + "Something went wrong while deleting.");
                }
                player.closeInventory();
                break;
            case "Overwrite Location":
                player.performCommand("sethome " + homeName + " overwrite");
                player.closeInventory();
                break;

        }
    }

    @EventHandler
    public void onIconEditClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().startsWith("Choose Icon:")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();

        String homeName = plugin.getIconSessions().get(player.getUniqueId());
        if (homeName == null) return;


        YamlConfiguration data = plugin.getHomeData(player);

        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!clicked.hasItemMeta()) return;

        String title = e.getView().getTitle();
        int currentPage = Integer.parseInt(title.split(" ")[3].split("/")[0]) - 1;

        if (clicked.getType() == Material.ARROW) {
            if (clicked.getItemMeta().getDisplayName().contains("Previous")) {
                IconEditGUI.openIconEditGUI(player, currentPage - 1);
            } else if (clicked.getItemMeta().getDisplayName().contains("Next")) {
                IconEditGUI.openIconEditGUI(player, currentPage + 1);
            }
            return;
        }

        Material selectedMaterial = clicked.getType();
        player.sendMessage("§aYou selected: §e" + selectedMaterial.name());

        data.set(homeName + ".icon", selectedMaterial.name());

        try {
            data.save(plugin.getHomeFile(player));
            player.sendMessage(ChatColor.GREEN + "Icon updated for home '" + homeName + "'");
        } catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong while saving.");
        }

        plugin.getIconSessions().remove(player.getUniqueId());

        player.closeInventory();
    }
}
