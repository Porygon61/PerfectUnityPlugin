package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class HomeEditGUI {

    public static void open(Player player, String homeName) {
        Inventory gui = Bukkit.createInventory(null, 9, "Edit Home: " + homeName);

        // Rename
        ItemStack rename = new ItemStack(Material.NAME_TAG);
        ItemMeta renameMeta = rename.getItemMeta();
        renameMeta.setDisplayName(ChatColor.YELLOW + "Rename");
        rename.setItemMeta(renameMeta);
        gui.setItem(3, rename);

        // Delete
        ItemStack delete = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Delete");
        delete.setItemMeta(deleteMeta);
        gui.setItem(5, delete);

        // Icon
        ItemStack icon = new ItemStack(Material.CLOCK);
        ItemMeta iconMeta = delete.getItemMeta();
        iconMeta.setDisplayName(ChatColor.BLUE + "Icon");
        icon.setItemMeta(iconMeta);
        gui.setItem(1, icon);

        // Overwrite Location
        ItemStack overwrite = new ItemStack(Material.MAP);
        ItemMeta overwriteMeta = delete.getItemMeta();
        overwriteMeta.setDisplayName(ChatColor.WHITE + "Overwrite Location");
        overwrite.setItemMeta(overwriteMeta);
        gui.setItem(7, overwrite);

        player.openInventory(gui);

        perfectUnityPlugin.getInstance().getEditSessions().put(player.getUniqueId(), homeName);
    }
}
