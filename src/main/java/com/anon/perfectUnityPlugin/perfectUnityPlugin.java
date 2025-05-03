package com.anon.perfectUnityPlugin;

import com.anon.perfectUnityPlugin.HomeManager.*;
import com.anon.perfectUnityPlugin.HomeManager.ChatListener;
import com.anon.perfectUnityPlugin.HomeManager.GUIListener;
import com.anon.perfectUnityPlugin.Scoreboard.Clock;
import com.anon.perfectUnityPlugin.Scoreboard.JoinListener;
import com.anon.perfectUnityPlugin.Scoreboard.ScoreboardUpdater;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.HashMap;
import java.util.Map;


public class perfectUnityPlugin extends JavaPlugin {

    private static perfectUnityPlugin instance;
    private int itemsPerPage;
    private int maxPages;
    private int guiSize;
    private int maxHomes;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Creates the config.yml file if missing

        itemsPerPage = getConfig().getInt("homes.gui.pagination.items-per-page", 36);
        maxHomes = getConfig().getInt("homes.max-homes", -1);
        guiSize = getConfig().getInt("homes.gui.gui-size", 45);

        //HomeManager
        getCommand("sethome").setExecutor(new SetHomeCommand(this));
        getCommand("sethome").setTabCompleter(new HomeTabCompleter(this));

        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("home").setTabCompleter(new HomeTabCompleter(this));


        getServer().getPluginManager().registerEvents(new GUIListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);


        //Scoreboard
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        new ScoreboardUpdater(this).runTaskTimer(this, 0L, 1200L);
        new Clock(this).runTaskTimer(this, 0L, 20L);


        getLogger().info("The perfectUnityPlugin is enabled!");
    }

    public YamlConfiguration getHomeData(Player player) {
        File file = new File(getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        if (!file.exists()) return null;
        return YamlConfiguration.loadConfiguration(file);
    }

    public File getHomeFile(Player player) {
        File file = new File(getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        if (!file.exists()) return null;
        return file;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public int getGuiSize() {
        return guiSize;
    }

    public int getMaxHomes() {
        return maxHomes;
    }


    private final Map<UUID, YamlConfiguration> homeGUIs = new HashMap<>();
    public Map<UUID, YamlConfiguration> getHomeGUIs() {
        return homeGUIs;
    }

    // Home being edited by player
    private final Map<UUID, String> editSessions = new HashMap<>();
    public Map<UUID, String> getEditSessions() {
        return editSessions;
    }

    // Home Icon being chosen
    private final Map<UUID, String> iconSessions = new HashMap<>();
    public Map<UUID, String> getIconSessions() {
        return iconSessions;
    }

    private final Map<UUID, String> renameQueue = new HashMap<>();
    public Map<UUID, String> getRenameQueue() {
        return renameQueue;
    }

    public static perfectUnityPlugin getInstance() {
        return instance;
    }

}
