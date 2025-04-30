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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!e.getView().getTitle().startsWith("Your Homes")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (!clicked.hasItemMeta()) return;


        String homeName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        YamlConfiguration data = perfectUnityPlugin.getInstance().getHomeGUIs().get(player.getUniqueId());


        // Make sure the clicked name exists as a home
        if (data == null || !data.contains(homeName)) return;

        String title = e.getView().getTitle();
        if (clicked.getType() == Material.ARROW) {
            if (clicked.getItemMeta().getDisplayName().contains("Previous")) {
                int currentPage = Integer.parseInt(title.split(" ")[2].split("/")[0]) - 1;
                perfectUnityPlugin.getInstance().getServer().getScheduler().runTask(
                        perfectUnityPlugin.getInstance(),
                        () -> new HomeCommand().openHomeGUI(player, perfectUnityPlugin.getInstance().getHomeGUIs().get(player.getUniqueId()), currentPage - 1)
                );
                return;
            } else if (clicked.getItemMeta().getDisplayName().contains("Next")) {
                int currentPage = Integer.parseInt(title.split(" ")[2].split("/")[0]) - 1;
                perfectUnityPlugin.getInstance().getServer().getScheduler().runTask(
                        perfectUnityPlugin.getInstance(),
                        () -> new HomeCommand().openHomeGUI(player, perfectUnityPlugin.getInstance().getHomeGUIs().get(player.getUniqueId()), currentPage + 1)
                );
                return;
            }
        }


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

        String homeName = perfectUnityPlugin.getInstance().getEditSessions().get(player.getUniqueId());
        if (homeName == null) return;

        File file = new File(perfectUnityPlugin.getInstance().getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

        String action = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());

        switch (action) {
            case "Icon":
                player.closeInventory();
                IconEditGUI.open(player, homeName);
                break;

            case "Rename":
                player.closeInventory();
                player.sendMessage(ChatColor.AQUA + "Type a new name in chat to rename home '" + homeName + "'");
                perfectUnityPlugin.getInstance().getRenameQueue().put(player.getUniqueId(), homeName);
                break;

            case "Delete":
                data.set(homeName, null);
                perfectUnityPlugin.getInstance().getEditSessions().remove(player.getUniqueId());
                try {
                    data.save(file);
                    player.sendMessage(ChatColor.RED + "Deleted home '" + homeName + "'");
                } catch (Exception ex) {
                    player.sendMessage(ChatColor.DARK_RED + "Something went wrong while deleting.");
                }
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

        String homeName = perfectUnityPlugin.getInstance().getEditSessions().get(player.getUniqueId());
        if (homeName == null) return;

        File file = new File(perfectUnityPlugin.getInstance().getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

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
            data.save(file);
            player.sendMessage(ChatColor.GREEN + "Icon updated for home '" + homeName + "'");
        } catch (Exception ex) {
            player.sendMessage(ChatColor.DARK_RED + "Something went wrong while saving.");
        }

        perfectUnityPlugin.getInstance().getIconSessions().remove(player.getUniqueId());

        player.closeInventory();
    }
}
