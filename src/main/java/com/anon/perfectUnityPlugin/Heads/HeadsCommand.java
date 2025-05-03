package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HeadsCommand implements CommandExecutor {
    private final perfectUnityPlugin plugin;

    public HeadsCommand(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        YamlConfiguration data = plugin.getHeadsPlayerData(player);

        //args come here if needed

        openHeadsGUI(player, data, 0);
        return true;
    }

    public void openHeadsGUI(Player player, YamlConfiguration data, int page) {

    }
}
