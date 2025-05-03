package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class HomeCommand implements CommandExecutor {

    private final perfectUnityPlugin plugin;

    public HomeCommand(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        YamlConfiguration data = plugin.getHomeData(player);

        if (args.length == 1) {
            //Direct teleport: /home <name>
            String homeName = args[0].toLowerCase();
            if (!data.contains(homeName)) {
                player.sendMessage("Home '" + homeName + "' does not exist.");
                return true;
            }

            Location loc = getHomeLocation(data, homeName);
            if (loc == null) {
                player.sendMessage("Failed to load home Location.");
                return true;
            }

            player.teleport(loc);
            player.sendMessage("Teleported to '" + homeName + "'");
            return true;
        } else {
            //Open GUI: /home
            openHomeGUI(player, data, 0);
            return true;
        }
    }

    private Location getHomeLocation(YamlConfiguration data, String homeName) {
        try {
            World world = Bukkit.getWorld(data.getString(homeName + ".world"));
            double x = data.getDouble(homeName + ".x");
            double y = data.getDouble(homeName + ".y");
            double z = data.getDouble(homeName + ".z");
            float yaw = (float) data.getDouble(homeName + ".yaw");
            //float pitch = (float) data.getDouble(homeName + ".pitch");

            return new Location(world, x, y, z, yaw, 0);
        } catch (Exception e) {
            return null;
        }
    }

    public void openHomeGUI(Player player, YamlConfiguration data, int page) {
        List<String> allHomes = (data == null) ? new ArrayList<>() : new ArrayList<>(data.getKeys(false));

        int size =  plugin.getConfig().getInt("gui-size", 54);
        int homesPerPage = plugin.getItemsPerPage();
        int totalPages = (int) Math.ceil(allHomes.size() / (double) homesPerPage);

        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory gui = Bukkit.createInventory(null, size, "Your Homes: Page " + (page + 1) + "/" + totalPages);
        int start = page * homesPerPage;
        int end = Math.min(start + homesPerPage, allHomes.size());

        // Place the home icons
        for (int i = start; i < end; i++) {
            String homeName = allHomes.get(i);
            // Get icon from YAML
            String iconName = data.getString(homeName + ".icon", "BEACON"); //Eventually swap with custom head
            Material iconMat = Material.matchMaterial(iconName);
            if (iconMat == null || !iconMat.isItem()) iconMat = Material.BEACON;

            ItemStack item = new ItemStack(iconMat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + homeName);
            Location loc = getHomeLocation(data, homeName);
            if (loc == null) continue;
            meta.setLore(null);
            meta.setLore(List.of(
                    ChatColor.GREEN + "X: " + ChatColor.WHITE + String.format("%.1f", loc.getX()),
                    ChatColor.GREEN + "Y: " + ChatColor.WHITE + String.format("%.1f", loc.getY()),
                    ChatColor.GREEN + "Z: " + ChatColor.WHITE + String.format("%.1f", loc.getZ()),
                    ChatColor.GRAY + "Left-click to teleport",
                    ChatColor.GRAY + "Right-click to edit"
            ));
            item.setItemMeta(meta);
            int slot = i - start;
            gui.setItem(slot, item);
        }

        // Navigation arrows
        if (page > 0) {
            gui.setItem(45, navItem(Material.ARROW, "§aPrevious Page"));
        }
        if (page < totalPages - 1) {
            gui.setItem(53, navItem(Material.ARROW, "§aNext Page"));
        }

        player.openInventory(gui);
        perfectUnityPlugin.getInstance().getHomeGUIs().put(player.getUniqueId(), data);
    }

    private ItemStack navItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public void openHomeGUI(Player player, YamlConfiguration data) {
        openHomeGUI(player, data, 0);
    }
}
