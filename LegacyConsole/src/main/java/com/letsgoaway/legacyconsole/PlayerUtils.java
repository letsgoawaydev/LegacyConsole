package com.letsgoaway.legacyconsole;

import org.bukkit.entity.Player;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;

public class PlayerUtils {
    /*
     * 1.16(20w22a)+ = not legacy
     *
     * 1.8 - 1.16(20w21a) = legacy
     *
     * helps with font stuffs
     */
    public static Boolean isCustomFontsSupported(Player player) {
        ViaAPI api = Via.getAPI(); // Get the API
        int version = api.getPlayerVersion(player); // Get the protocol version
        if (version >= 718) return false;
        else return true;
    }
}
