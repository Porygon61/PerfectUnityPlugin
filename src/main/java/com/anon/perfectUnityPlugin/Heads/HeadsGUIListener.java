package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.anon.perfectUnityPlugin.Heads.HeadsCommand.createCustomHead;
import static com.anon.perfectUnityPlugin.Heads.HeadsCommand.createPlayerHead;

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
        YamlConfiguration headsData = plugin.getHeadsGUIs().get(player.getUniqueId());
        YamlConfiguration playerData = plugin.getHeadsPlayerData(player);

        if (headsData == null || !headsData.contains(headName)) return;

        switch (e.getClick()) {
            case LEFT:
                if (Objects.equals(playerData.get("give-permission"), false)) {
                    player.sendMessage("You are not authorized to receive Heads");
                    break;
                }
                SetAmount.openGiveGUI(player, headName, headsData);
                break;

            case RIGHT:
                break;

            default:
                break;
        }
    }

    private final Map<UUID, Integer> giveAmounts = new HashMap<>();
    @EventHandler
    public void onSetAmountClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (!(ChatColor.stripColor(e.getView().getTitle()).startsWith("Give Head"))) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String action = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        String headName = plugin.getAmountSetSession().get(player.getUniqueId());
        if (headName == null) return;

        YamlConfiguration playerData = plugin.getHeadsPlayerData(player);
        YamlConfiguration headsData = plugin.getHeadsData();

        int amount = giveAmounts.getOrDefault(player.getUniqueId(), 1);

        switch (action.toLowerCase()) {
            case "confirm":
                String type = headsData.getString(headName + ".type");
                ItemStack headItem = null;

                if ("player".equalsIgnoreCase(type)) {
                    headItem = createPlayerHead(headName, headsData);
                } else if ("custom".equalsIgnoreCase(type)) {
                    headItem = createCustomHead(headName, headsData);
                }

                if (headItem == null) {
                    player.sendMessage(ChatColor.RED + "Could not create head.");
                    return;
                }

                int max = playerData.getInt("balance", -1);
                if (max >= 0 && amount > max) {
                    player.sendMessage(ChatColor.RED + "You can't get more than " + max);
                    return;
                }

                headItem.setAmount(amount);
                player.getInventory().addItem(headItem);

                giveAmounts.remove(player.getUniqueId());
                plugin.getAmountSetSession().remove(player.getUniqueId());
                player.closeInventory();
                break;

            case "increase":
                amount++;
                giveAmounts.put(player.getUniqueId(), amount);
                updateAmountDisplay(e, amount);
                break;

            case "decrease":
                if (amount > 1) amount--;
                giveAmounts.put(player.getUniqueId(), amount);
                updateAmountDisplay(e, amount);
                break;
        }
    }

    private void updateAmountDisplay(InventoryClickEvent e, int amount) {
        ItemStack amountDisplay = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = amountDisplay.getItemMeta();
        meta.setDisplayName("§eAmount: §f" + amount);
        amountDisplay.setItemMeta(meta);
        e.getInventory().setItem(4, amountDisplay);
    }
}
