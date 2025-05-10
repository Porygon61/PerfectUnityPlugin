package com.anon.perfectUnityPlugin.Heads;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CCHeadCommand implements CommandExecutor {

    private final perfectUnityPlugin plugin;

    public CCHeadCommand(perfectUnityPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length < 2) {
            player.sendMessage("Usage: /cchead <name> <base64> [name-color]");
            return true;
        }
        String name = args[0];
        String base64 = args[1];
        String color = args.length > 2 ? interpretColor(args[2]) : "§e";

        YamlConfiguration data = plugin.getHeadsData();

        data.set(name.toLowerCase() + ".type", "custom");
        data.set(name.toLowerCase() + ".texture", base64);
        data.set(name.toLowerCase() + ".display-color", color);
        data.set(name.toLowerCase() + ".display", name);

        try {
            data.save(plugin.getHeadsFile());
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("Failed to save Head.");
            return true;
        }

        player.sendMessage("Head '" + name + "' has been created!");
        return true;
    }

    public String interpretColor(String input) {
        if (input.equals("Black")) return "§0";
        if (input.equals("DarkBlue")) return "§1";
        if (input.equals("DarkGreen")) return "§2";
        if (input.equals("DarkAqua")) return "§3";
        if (input.equals("DarkRed")) return "§4";
        if (input.equals("DarkPurple")) return "§5";
        if (input.equals("Gold")) return "§6";
        if (input.equals("Gray")) return "§7";
        if (input.equals("DarkGray")) return "§8";
        if (input.equals("Blue")) return "§9";
        if (input.equals("Green")) return "§a";
        if (input.equals("Aqua")) return "§b";
        if (input.equals("Red")) return "§c";
        if (input.equals("LightPurple")) return "§d";
        if (input.equals("Yellow")) return "§e";
        if (input.equals("White")) return "§f";
        return "Green";
    }
}
