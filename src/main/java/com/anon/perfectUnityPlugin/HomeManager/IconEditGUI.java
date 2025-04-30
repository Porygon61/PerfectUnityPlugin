package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IconEditGUI {

    private static final List<Material> ICONS = new ArrayList<>();

    static {
        for (Material mat : Material.values()) {
            if (mat.isItem() && mat != Material.AIR) {
                ICONS.add(mat);
            }
        }
    }

    public static void openIconEditGUI(Player player, int page) {
        int itemsPerPage = 45;
        int totalPages = (int) Math.ceil(ICONS.size() / (double) itemsPerPage);

        Inventory inv = Bukkit.createInventory(null, 54,
                "Choose Icon: Page " + (page + 1) + "/" + totalPages);

        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, ICONS.size());

        for (int i = start; i < end; i++) {
            Material mat = ICONS.get(i);
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.YELLOW + mat.name());
            item.setItemMeta(meta);
            inv.setItem(i - start, item);
        }

        if (page > 0) {
            inv.setItem(45, navItem(Material.ARROW, "§aPrevious Page"));
        }

        if (end < ICONS.size()) {
            inv.setItem(53, navItem(Material.ARROW, "§aNext Page"));
        }

        player.openInventory(inv);
    }

    private static ItemStack navItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    // Shortcuts for easier external calls
    public static void open(Player player, String homeName) {
        perfectUnityPlugin.getInstance().getIconSessions().put(player.getUniqueId(), homeName);
        openIconEditGUI(player, 0);
    }
}
