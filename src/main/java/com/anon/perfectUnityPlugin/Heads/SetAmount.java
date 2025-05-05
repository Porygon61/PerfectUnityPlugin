package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SetAmount {

    public static void openGiveGUI(Player player, String headName, YamlConfiguration headsData) {
        //gui logic
        Inventory gui = Bukkit.createInventory(null, 27, "Give Head");

        // Increase Amount
        ItemStack increase = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta increaseMeta = increase.getItemMeta();
        increaseMeta.setDisplayName("§2§oIncrease");
        increase.setItemMeta(increaseMeta);
        gui.setItem(12, increase);

        // Confirm
        String type = headsData.getString(headName + ".type");
        ItemStack headItem = null;
        if ("player".equalsIgnoreCase(type)) {
            headItem = createPlayerHead(headName, headsData);
        } else if ("custom".equalsIgnoreCase(type)) {
            headItem = createCustomHead(headName, headsData);
        }

        ItemStack confirm = headItem;
        gui.setItem(14, confirm);

        // Decrease
        ItemStack decrease = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta decreaseMeta = decrease.getItemMeta();
        decreaseMeta.setDisplayName("§2§oDecrease");
        decrease.setItemMeta(decreaseMeta);
        gui.setItem(16, decrease);

        player.openInventory(gui);
        perfectUnityPlugin.getInstance().getAmountSetSession().put(player.getUniqueId(), headName);
    }

    public static ItemStack createCustomHead(String name, YamlConfiguration headsData) {
        ItemStack customHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) customHead.getItemMeta();

        meta.setDisplayName("§a§lCONFIRM");

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "CustomHead");
        String texture = headsData.getString(name + ".texture");
        if (texture == null || texture.isEmpty()) return null;
        profile.getProperties().add(new ProfileProperty("textures", texture));

        meta.setOwnerProfile(profile);
        customHead.setItemMeta(meta);
        return customHead;
    }

    public static ItemStack createPlayerHead(String name, YamlConfiguration headsData) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

        UUID uuid = UUID.fromString(headsData.getString(name + ".owner-uuid"));
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

        meta.setDisplayName("§a§lCONFIRM");

        playerHead.setItemMeta(meta);
        return playerHead;
    }
}
