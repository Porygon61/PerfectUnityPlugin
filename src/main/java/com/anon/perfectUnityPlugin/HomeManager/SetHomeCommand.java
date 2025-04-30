package com.anon.perfectUnityPlugin.HomeManager;

import com.anon.perfectUnityPlugin.perfectUnityPlugin;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class SetHomeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length < 1) {
            player.sendMessage("Usage: /sethome <name> [overwrite]");
            return true;
        }

        String homeName = args[0].toLowerCase();
        boolean isOverwrite = args.length > 1 && args[1].equalsIgnoreCase("overwrite");

        //Load player file
        File file = new File(perfectUnityPlugin.getInstance().getDataFolder(), "homes/" + player.getUniqueId() + ".yml");
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

        //Check max homes if not overwriting
        if (!data.contains(homeName) && !isOverwrite) {
            int maxHomes = perfectUnityPlugin.getInstance().getConfig().getInt("max-homes", -1);
            if (maxHomes != -1 && data.getKeys(false).size() >= maxHomes) {
                player.sendMessage("You reached the max number of Home Slots");
                return true;
            }
        }

        // Prevent overwriting if not explicitly allowed
        if (data.contains(homeName) && !isOverwrite) {
            player.sendMessage("A Home with that name already exists. Use '/sethome <name> overwrite' to replace it.");
            return true;
        }

        // Save Location
        Location loc = player.getLocation();
        data.set(homeName + ".world", loc.getWorld().getName());
        data.set(homeName + ".x", loc.getX());
        data.set(homeName + ".y", loc.getY());
        data.set(homeName + ".z", loc.getZ());
        data.set(homeName + ".yaw", loc.getYaw());
        data.set(homeName + ".pitch", loc.getPitch());

        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("Failed to save Home.");
            return true;
        }

        player.sendMessage("Home '" + homeName + "' has been set!");
        return true;
    }
}
