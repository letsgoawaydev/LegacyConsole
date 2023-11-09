package com.letsgoaway.legacyconsole;

import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;



public class PlayerInputTexts {
    public enum Use {
        UNKNOWN(ChatColor.GRAY + "[Use]"), SWITCH("\uE046"), PLAYSTATION("\uE026"), XBOX("\uE006"), KEYBOARD_MOUSE("\uE071"), TOUCH("\uE018");

        String icon = "";

        Use(String string) {
            this.icon = string;
        }

        public static String getIcon(Player player) {
            if (!Editions.isOnBedrock(player))
            {
                if (PlayerUtils.isCustomFontsSupported(player))
                {
                    return PlayerInputTexts.Use.UNKNOWN.icon;
                }
            }
            switch (PlayerInputType.getInputType(player)) {
                case UNKNOWN:
                    return PlayerInputTexts.Use.UNKNOWN.icon;
                case CONTROLLER:
                    switch (PlayerPlatform.getPlayerPlatform(player)) {
                        case PLAYSTATION:
                            return PlayerInputTexts.Use.PLAYSTATION.icon;
                        case SWITCH:
                            return PlayerInputTexts.Use.SWITCH.icon;
                        case XBOX:
                            return PlayerInputTexts.Use.XBOX.icon;
                        default:
                            return PlayerInputTexts.Use.XBOX.icon;
                    }
                case KEYBOARD_MOUSE:
                    return PlayerInputTexts.Use.KEYBOARD_MOUSE.icon;
                case TOUCH:
                    return PlayerInputTexts.Use.TOUCH.icon;
                case VR:
                    return PlayerInputTexts.Use.UNKNOWN.icon;
                default:
                    return PlayerInputTexts.Use.KEYBOARD_MOUSE.icon;
            }
        }
    }
}

