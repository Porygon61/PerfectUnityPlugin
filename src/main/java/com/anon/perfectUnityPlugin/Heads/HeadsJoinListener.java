package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.Utility;
import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HeadsJoinListener implements Listener {
    private final perfectUnityPlugin plugin;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public HeadsJoinListener(perfectUnityPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        saveHead(player);
        createHeadsPlayerFile(player);
        checkHeadsAllowance(player);
    }

    public void saveHead(Player player) {
        YamlConfiguration data = plugin.getHeadsData();
        String name = player.getName().toLowerCase();
        data.set(name + ".type", "player");
        data.set(name + ".owner", player.getName());
        data.set(name + ".owner-uuid", player.getUniqueId().toString());
        data.set(name + ".texture", Utility.getInstance().getBase64FromURL(player));
        if (!data.contains(name + ".display-color")) {
            data.set(name + ".display-color", "§6");
        }
        if (!data.contains(name + ".display")) {
            data.set(name + ".display", player.getName());
        }

        try {
            data.save(plugin.getHeadsFile());
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void createHeadsPlayerFile(Player player) {
        YamlConfiguration data = plugin.getHeadsPlayerData(player);
        FileConfiguration config = plugin.getConfig();
        if (!data.contains("access-permission")) {
            data.set("access-permission", config.getBoolean("heads.access-permission", true));
        }
        if (!data.contains("give-permission")) {
            data.set("give-permission", config.getBoolean("heads.give-permission", true));
        }
        if (!data.contains("balance-class")) {
            data.set("balance-class", "default");
        }
        if (!data.contains("balance")) {
            data.set("balance", config.getDouble("heads.settings.balance-class.default", -1));
        }
        if (config.getBoolean("heads.settings.allowance.enabled", false)) {
            if (!data.contains("last-allowance-received")) {
                data.set("last-allowance-received", dateFormat.format(new Date()));
            }
        }
        try {
            data.save(plugin.getHeadsPlayerFile(player));
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void checkHeadsAllowance(Player player) {
        FileConfiguration config = plugin.getConfig();
        if (!config.getBoolean("heads.settings.allowance.enabled", false)) {
            return;
        }

        String frequency = config.getString("heads.settings.allowance.frequency", "weekly");
        YamlConfiguration data = plugin.getHeadsPlayerData(player);
        String lastReceivedStr = data.getString("last-allowance-received");

        if (lastReceivedStr == null) {
            giveAllowance(player, data);
            return;
        }

        try {
            Date lastReceived = dateFormat.parse(lastReceivedStr);
            long now = System.currentTimeMillis();
            long interval = switch (frequency.toLowerCase()) {
                case "weekly" -> 7L * 24 * 60 * 60 * 1000;
                case "monthly" -> 30L * 24 * 60 * 60 * 1000;
                case "yearly" -> 365L * 24 * 60 * 60 * 1000;
                default -> 0L;
            };

            if (now - lastReceived.getTime() >= interval) {
                giveAllowance(player, data);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            giveAllowance(player, data); // Fallback in case of parse error
        }
    }

    private void giveAllowance(Player player, YamlConfiguration data) {
        double allowance = getBalanceClass(player);
        double balance = data.getDouble("balance", 0);
        data.set("balance", balance + allowance);
        data.set("last-allowance-received", dateFormat.format(new Date()));

        try {
            data.save(plugin.getHeadsPlayerFile(player));
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.sendMessage("§aYou have received your heads allowance!");
    }

    private int getBalanceClass(Player player) {
        YamlConfiguration data = plugin.getHeadsPlayerData(player);
        FileConfiguration config = plugin.getConfig();

        List<String> configClasses =  new ArrayList<>(config.getKeys(true));

        Map<String, Integer> classes = new HashMap<>();
        for (String configClass : configClasses) {
            if (configClass.startsWith("heads.settings.balance-class.")) {
                String className = configClass.replace("heads.settings.balance-class.", "").toLowerCase();
                classes.put(className, config.getInt(configClass));
            }

        }

        return classes.getOrDefault((data.getString("balance-class")).toLowerCase(), -1);
    }
}
