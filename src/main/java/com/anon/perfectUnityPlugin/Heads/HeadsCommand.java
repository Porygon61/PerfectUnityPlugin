package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HeadsCommand implements CommandExecutor {
    private final perfectUnityPlugin plugin;

    public HeadsCommand(perfectUnityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        YamlConfiguration headsData = plugin.getHeadsData();
        YamlConfiguration playerData = plugin.getHeadsPlayerData(player);

        if (!playerData.getBoolean("access-permission")) {
            player.sendMessage("You are not allowed to use the heads Command");
            return true;
        }

        if (args.length == 0) {
            openHeadsGUI(player, plugin.getHeadsData(), 0);
            return true;
        }

        if (args.length <= 2) {
            String headName = args[0].toLowerCase();
            int amount = 1;
            if (args.length == 2) {
                try {
                    amount = Integer.parseInt(args[1]);
                } catch (NumberFormatException ex) {
                    player.sendMessage("Amount must be a number.");
                    return true;
                }
            }
            if (!headsData.contains(headName)) {
                player.sendMessage("Head '" + headName + "' does not exist.");
                return true;
            }
            if (playerData.getInt("balance") < amount && !(playerData.getInt("balance") == -1)  ) {
                player.sendMessage("You don't have enough money to get this amount.");
            }

            String type = headsData.getString(headName + ".type");
            ItemStack headItem = null;
            if ("player".equalsIgnoreCase(type)) {
                headItem = createPlayerHead(headName, headsData);
            } else if ("custom".equalsIgnoreCase(type)) {
                headItem = createCustomHead(headName, headsData);
            }
            if (headItem != null) {
                headItem.setAmount(amount);
                player.getInventory().addItem(headItem);
                player.sendMessage("You received §a" + amount + "x " + headName);
            }
            return true;
        }
        // Too many args
        player.sendMessage("Usage: /heads [<name> [amount]]");
        return true;
    }

    public void openHeadsGUI(Player player, YamlConfiguration headsData, int page) {
        List<String> heads = (headsData == null) ? new ArrayList<>() : new ArrayList<>(headsData.getKeys(false));

        int size = plugin.getGuiSize();
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
        plugin.getHeadsGUIs().put(player.getUniqueId(), headsData);
    }

    public static ItemStack createCustomHead(String name, YamlConfiguration headsData) {
        ItemStack customHead = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) customHead.getItemMeta();

        String displayColor = headsData.getString(name + ".display-color", "§7");
        String displayName = headsData.getString(name + ".display", "Unknown Head");
        meta.setDisplayName(displayColor + displayName);


        PlayerProfile profile = perfectUnityPlugin.getInstance().getHeadProfile(name);
        if (profile == null) {
            // it wasn’t in the map—must’ve been added at runtime!
            String texture = headsData.getString(name + ".texture", "");
            UUID id = UUID.nameUUIDFromBytes(("perfectUnity:" + name).getBytes(StandardCharsets.UTF_8));
            profile = Bukkit.createProfile(id, "CustomHead");
            profile.getProperties().add(new ProfileProperty("textures", texture));
            // cache it so next time it’s found immediately
            perfectUnityPlugin.getInstance().getHeadProfiles().put(name, profile);
        }

        meta.setOwnerProfile(profile);
        customHead.setItemMeta(meta);
        return customHead;
    }

    public static ItemStack createPlayerHead(String name, YamlConfiguration headsData) {
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
