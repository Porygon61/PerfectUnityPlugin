package com.anon.perfectUnityPlugin;

import java.util.Base64;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerTextures;

public class Utility {
    private static Utility instance = new Utility();

    private Utility() {
        instance = this;
    }

    public static Utility getInstance() { return instance; }


    public String getBase64FromURL(Player player) {
        PlayerTextures textures = player.getPlayerProfile().getTextures();

        if (textures == null || textures.getSkin() == null) {
            return null; // No skin available
        }

        String skinUrl = textures.getSkin().toString();

        JsonObject texturesObj = new JsonObject();
        JsonObject skin = new JsonObject();
        skin.addProperty("url", skinUrl);

        JsonObject skinWrapper = new JsonObject();
        skinWrapper.add("SKIN", skin);

        texturesObj.add("textures", skinWrapper);

        // Encode as Base64
        String base64 = Base64.getEncoder().encodeToString(texturesObj.toString().getBytes());
        return base64;
    }

    public String getURLFromBase64(String base64) {
        try {
            // Decode Base64 to JSON string
            String json = new String(Base64.getDecoder().decode(base64));
            Bukkit.broadcastMessage(json);

            // Parse JSON
            JsonObject texturesObj = JsonParser.parseString(json).getAsJsonObject();
            JsonObject skinWrapper = texturesObj.getAsJsonObject("textures");
            JsonObject skin = skinWrapper.getAsJsonObject("SKIN");

            // Extract the URL
            return skin.get("url").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
