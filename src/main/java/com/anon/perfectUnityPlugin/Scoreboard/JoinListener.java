package com.anon.perfectUnityPlugin.Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setScoreboard(player);
    }

    public static void setScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();


        Objective objective = board.registerNewObjective("Scoreboard", "dummy", ChatColor.BLUE + "SERVER");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);


        // Name
        Score nameLine = objective.getScore(ChatColor.YELLOW + "Name: " + ChatColor.WHITE + player.getName());
        nameLine.setScore(5);


        //Kill Counter
        Score killLine = objective.getScore(ChatColor.YELLOW + "Mob Kills: " + ChatColor.WHITE + player.getStatistic(Statistic.MOB_KILLS));
        killLine.setScore(4);


        // Death Counter
        Score deathLine = objective.getScore(ChatColor.YELLOW + "Deaths: " + ChatColor.WHITE + player.getStatistic(Statistic.DEATHS));
        deathLine.setScore(3);


        //Last Death Location
        Location deathLoc = player.getLastDeathLocation();
        String coords = null;
        String deathWorld = deathLoc == null ? "No Death yet" : deathLoc.getWorld().getName();
        if (deathLoc != null) {
            int x = deathLoc.getBlockX();
            int y = deathLoc.getBlockY();
            int z = deathLoc.getBlockZ();

            coords = x + ", " + y + ", " + z;
            // Use this in your scoreboard or message
        } else {
            coords = "Unknown";
            deathWorld = "Unknown";
        }


        Score lastDeathLine = objective.getScore(ChatColor.YELLOW + "Last Death: " + ChatColor.WHITE + deathLoc.getWorld().getName());
        lastDeathLine.setScore(2);

        Score lastDeathCoordsLine = objective.getScore(ChatColor.WHITE + "-> " + coords);
        lastDeathCoordsLine.setScore(1);


        player.setScoreboard(board);
    }
}
