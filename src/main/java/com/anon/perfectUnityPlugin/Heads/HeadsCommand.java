package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.Utility;
import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import jdk.jshell.execution.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HeadsCommand implements CommandExecutor {
    private final perfectUnityPlugin plugin;

    public HeadsCommand(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        YamlConfiguration headsData = plugin.getHeadsData();

        //args come here if needed

        openHeadsGUI(player, headsData, 0);
        return true;
    }

    public void openHeadsGUI(Player player, YamlConfiguration headsData, int page)  {
        List<String> heads = (headsData == null) ? new ArrayList<>() : new ArrayList<>(headsData.getKeys(false));

        int size =  plugin.getGuiSize();
        int itemsPerPage = plugin.getItemsPerPage();
        int totalPages = (int) Math.ceil(heads.size() / (double) itemsPerPage);

        page = Math.max(0, Math.min(page, totalPages - 1));

        Inventory gui = Bukkit.createInventory(null, size, "All Heads: Page " + (page + 1) + "/" + totalPages);
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, heads.size());

        // Placing the heads
        for (int i = start; i < end; i++) {
            String name = heads.get(i);
            String type = headsData.getString(name + ".type");

            ItemStack headItem = null;

            if ("player".equalsIgnoreCase(type)) {
                headItem = createPlayerHead(name, headsData);
            } else if ("custom".equalsIgnoreCase(type)) {
                headItem = createCustomHead(name, headsData);
            }

            if (headItem != null) {
                gui.setItem(i - start, headItem); // Slot within current page
            }
        }

        // NAV
        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            ItemMeta meta = prev.getItemMeta();
            meta.setDisplayName("§aPrevious Page");
            prev.setItemMeta(meta);
            gui.setItem(size - 9, prev);
        }

        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            ItemMeta meta = next.getItemMeta();
            meta.setDisplayName("§aNext Page");
            next.setItemMeta(meta);
            gui.setItem(size - 1, next);
        }

        player.openInventory(gui);
        plugin.getHeadGUIs().put(player.getUniqueId(), headsData);
    }

    public static ItemStack createCustomHead(String name, YamlConfiguration headsData)  {
        ItemStack customHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) customHead.getItemMeta();

        String displayColor = headsData.getString(name + ".display-color", "§7");
        String displayName = headsData.getString(name + ".display", "Unknown Head");
        meta.setDisplayName(displayColor + displayName);


        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "CustomHead");
        String texture = headsData.getString(name + ".texture");
        if (texture == null || texture.isEmpty()) return null;
        profile.getProperties().add(new ProfileProperty("textures", texture));

        meta.setOwnerProfile(profile);
        customHead.setItemMeta(meta);
        return customHead;
    }

    public ItemStack createPlayerHead(String name, YamlConfiguration headsData) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

        UUID uuid = UUID.fromString(headsData.getString(name + ".owner-uuid"));
        String displayName = (headsData.getString(name + ".display-color") + headsData.getString(name + ".display", "§7Unknown Player"));

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        meta.setDisplayName(displayName);

        playerHead.setItemMeta(meta);
        return playerHead;
    }

}
