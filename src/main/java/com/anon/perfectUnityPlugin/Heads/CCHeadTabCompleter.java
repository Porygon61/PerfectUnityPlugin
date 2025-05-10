package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CCHeadTabCompleter implements TabCompleter {
    private final perfectUnityPlugin plugin;

    public CCHeadTabCompleter(perfectUnityPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        if (args.length == 3) {
            List<String> list = new ArrayList<>();
            list.add("Black"); // §0
            list.add("DarkBlue"); // §1
            list.add("DarkGreen"); // §2
            list.add("DarkAqua"); // §3
            list.add("DarkRed"); // §4
            list.add("DarkPurple"); // §5
            list.add("Gold"); // §6
            list.add("Gray"); // §7
            list.add("DarkGray"); // §8
            list.add("Blue"); // §9
            list.add("Green"); // §a
            list.add("Aqua"); // §b
            list.add("Red"); // §c
            list.add("LightPurple"); // §d
            list.add("Yellow"); // §e
            list.add("White"); // §f

            return list;
        }
        return Collections.emptyList();
    }
}
