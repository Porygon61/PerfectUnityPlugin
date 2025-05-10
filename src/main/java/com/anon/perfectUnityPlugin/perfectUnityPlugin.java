package com.anon.perfectUnityPlugin;

import com.anon.perfectUnityPlugin.Heads.*;
import com.anon.perfectUnityPlugin.HomeManager.*;
import com.anon.perfectUnityPlugin.Scoreboard.Clock;
import com.anon.perfectUnityPlugin.Scoreboard.JoinListener;
import com.anon.perfectUnityPlugin.Scoreboard.ScoreboardUpdater;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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

        itemsPerPage = getConfig().getInt("gui.pagination.items-per-page", 36);
        maxHomes = getConfig().getInt("homes.max-homes", -1);
        guiSize = getConfig().getInt("gui.gui-size", 45);

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


        //Heads
        getCommand("heads").setExecutor(new HeadsCommand(this));
        getCommand("heads").setTabCompleter(new HeadsTabCompleter(this));

        getCommand("cchead").setExecutor(new CCHeadCommand(this));
        getCommand("cchead").setTabCompleter(new CCHeadTabCompleter(this));


        getServer().getPluginManager().registerEvents(new HeadsGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new HeadsJoinListener(this), this);


        //
        getLogger().info("The perfectUnityPlugin is enabled!");
    }

    // Heads
    public File getHeadsPlayerFile(Player player) {
        File file = new File(getDataFolder(), "heads/players/" + player.getUniqueId() + ".yml");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Ensure 'heads/players/' directory exists
                file.createNewFile();          // Create the YAML file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public YamlConfiguration getHeadsPlayerData(Player player) {
        return YamlConfiguration.loadConfiguration(getHeadsPlayerFile(player));
    }


    public File getHeadsFile() {
        File file = new File(getDataFolder(), "heads/heads.yml");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Ensure 'heads/' directory exists
                file.createNewFile();          // Create the YAML file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public YamlConfiguration getHeadsData() {
        return YamlConfiguration.loadConfiguration(getHeadsFile());
    }


    private final Map<UUID, YamlConfiguration> headGUIs = new HashMap<>();
    public Map<UUID, YamlConfiguration> getHeadsGUIs() {
        return headGUIs;
    }

    private final Map<UUID, String> amountSetSession = new HashMap<>();
    public Map<UUID, String> getAmountSetSession() {
        return amountSetSession;
    }


    // Homes
    public File getHomeFile(Player player) {
        File file = new File(getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // Ensure 'homes/' directory exists
                file.createNewFile();          // Create the YAML file
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public YamlConfiguration getHomeData(Player player) {
        return YamlConfiguration.loadConfiguration(getHomeFile(player));
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

    private final Map<UUID, String> editSessions = new HashMap<>();
    public Map<UUID, String> getEditSessions() {
        return editSessions;
    }

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
