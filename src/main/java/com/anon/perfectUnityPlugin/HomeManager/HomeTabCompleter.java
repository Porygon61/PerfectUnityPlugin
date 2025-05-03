package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HomeTabCompleter implements TabCompleter {

    private final perfectUnityPlugin plugin;

    public HomeTabCompleter(perfectUnityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        if (args.length == 1) {
            YamlConfiguration data = plugin.getHomeData(player);
            if (data == null) return Collections.emptyList();

            String input = args[0].toLowerCase();

            return data.getKeys(false).stream()
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .sorted()
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
