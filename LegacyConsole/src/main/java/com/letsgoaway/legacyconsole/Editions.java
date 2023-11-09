package com.letsgoaway.legacyconsole;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public enum Editions {
    BEDROCK("Bedrock Edition"), JAVA("Java Edition");

    String displayName = "";

    Editions(String displayName) {
        this.displayName = displayName;
    }

    public static boolean isOnBedrock(Player player) {
        return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    public static boolean isOnJava(Player player) {
        return !FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
    }

    public static Editions getEdition(Player player) {
        if (isOnBedrock(player))
        {
            return Editions.BEDROCK;
        }
        else
        {
            return Editions.JAVA;
        }
    }
}
