package com.anon.perfectUnityPlugin.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Clock extends BukkitRunnable {

    private final JavaPlugin plugin;
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");

    public Clock(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        String realTime = ChatColor.GOLD + "Real:" + ChatColor.WHITE + LocalTime.now().format(timeFormat);



        for (Player player : Bukkit.getOnlinePlayers()) {
            long ticks = player.getWorld().getTime(); // 0 to 24000
            int hours = (int)((ticks / 1000 + 6) % 24); // +6 to shift 0 ticks = 6 AM
            String mcTime = ChatColor.GRAY + "Time: " + hours + ":00";

            player.sendActionBar(realTime + mcTime);
        }
    }
}
