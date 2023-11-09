package com.letsgoaway.legacyconsole;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public enum PlayerInputType {
    UNKNOWN("Unknown"), KEYBOARD_MOUSE("Keyboard & Mouse"), TOUCH("Touchscreen"), CONTROLLER("Controller"), VR("Virtual Reality");

    String displayName = "";

    PlayerInputType(String displayName) {
        this.displayName = displayName;
    }

    public static PlayerInputType getInputType(Player player) {
        if (Editions.isOnBedrock(player))
        {
            switch (FloodgateApi.getInstance().getPlayer(player.getUniqueId()).getInputMode()) {
                case UNKNOWN:
                    return PlayerInputType.CONTROLLER;
                case KEYBOARD_MOUSE:
                    return PlayerInputType.KEYBOARD_MOUSE;
                case TOUCH:
                    return PlayerInputType.TOUCH;
                case CONTROLLER:
                    return PlayerInputType.CONTROLLER;
                case VR:
                    return PlayerInputType.VR;
                default:
                    return PlayerInputType.UNKNOWN;
            }
        }
        else
        {
            return PlayerInputType.KEYBOARD_MOUSE;
        }
    }
}
