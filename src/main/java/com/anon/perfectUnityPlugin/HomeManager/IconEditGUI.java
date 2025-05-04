package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class IconEditGUI {

    private static final List<Material> ICONS = new ArrayList<>();
    private static final int ITEMS_PER_PAGE = perfectUnityPlugin.getInstance().getItemsPerPage();
    private static final int GUI_SIZE = perfectUnityPlugin.getInstance().getGuiSize();

    static {
        for (Material mat : Material.values()) {
            if (mat.isItem() && mat != Material.AIR) {
                ICONS.add(mat);
            }
        }
    }

    public static void openIconEditGUI(Player player, int page) {
        OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        int iconPages = (int) Math.ceil(ICONS.size() / (double) ITEMS_PER_PAGE);
        int headPages = (int) Math.ceil(players.length / (double) ITEMS_PER_PAGE);
        int totalPages = iconPages + headPages;

        Inventory inv = Bukkit.createInventory(null, GUI_SIZE,
                ChatColor.GREEN + "Choose Icon: Page " + (page + 1) + "/" + totalPages);

        if (page < iconPages) {
            // ICON
            int start = page * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, ICONS.size());

            for (int i = start; i < end; i++) {
                Material mat = ICONS.get(i);
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.YELLOW + mat.name());
                item.setItemMeta(meta);
                inv.setItem(i - start, item);
            }
        } else {
            // HEAD
            int headPage = page - iconPages;
            int start = headPage * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, players.length);

            for (int i = start; i < end; i++) {
                OfflinePlayer p = players[i];
                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                if (meta == null) continue;
                meta.setOwningPlayer(p);
                meta.setDisplayName("§e" + (p.getName() != null ? p.getName() : "Unknown"));
                head.setItemMeta(meta);
                inv.setItem(i - start, head);
            }
        }

        // NAV
        int bottomRowStart = GUI_SIZE - 9;
        if (page > 0) {
            inv.setItem(bottomRowStart, navItem(Material.ARROW, "§aPrevious Page"));
        }

        if (page < totalPages - 1) {
            inv.setItem(bottomRowStart + 8, navItem(Material.ARROW, "§aNext Page"));
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
        openIconEditGUI(player, 0);
        perfectUnityPlugin.getInstance().getIconSessions().put(player.getUniqueId(), homeName);
    }
}
