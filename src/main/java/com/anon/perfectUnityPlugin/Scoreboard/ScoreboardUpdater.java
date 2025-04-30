package com.anon.perfectUnityPlugin.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {
    private final JavaPlugin plugin;


    public ScoreboardUpdater(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }

    private void updateScoreboard(Player player) {
        JoinListener.setScoreboard(player);
    }
}
