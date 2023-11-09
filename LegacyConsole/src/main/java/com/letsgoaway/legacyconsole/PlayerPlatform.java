package com.letsgoaway.legacyconsole;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public enum PlayerPlatform {
    UNKNOWN("Unknown"), ANDROID("Android"), IOS("iOS"), OSX("macOS"), AMAZON("Amazon"), GEARVR("Gear VR"), HOLOLENS("Hololens"), WINDOWS("Windows"), DEDICATED("Dedicated"), TVOS("Apple TV"),
    PLAYSTATION("Playstation"), SWITCH("Switch"), XBOX("Xbox"), WINDOWS_PHONE("Windows Phone"), JAVA("Java");

    String displayName = "";

    PlayerPlatform(String displayName) {
        this.displayName = displayName;
    }

    public static PlayerPlatform getPlayerPlatform(Player player) {
        if (Editions.isOnBedrock(player))
        {
            switch (FloodgateApi.getInstance().getPlayer(player.getUniqueId()).getDeviceOs()) {
                case UNKNOWN:
                    return PlayerPlatform.UNKNOWN;
                case GOOGLE:
                    return PlayerPlatform.ANDROID;
                case IOS:
                    return PlayerPlatform.IOS;
                case OSX:
                    return PlayerPlatform.OSX;
                case AMAZON:
                    return PlayerPlatform.AMAZON;
                case GEARVR:
                    return PlayerPlatform.GEARVR;
                case HOLOLENS:
                    return PlayerPlatform.HOLOLENS;
                case UWP:
                    return PlayerPlatform.WINDOWS;
                case WIN32:
                    return PlayerPlatform.WINDOWS;
                case DEDICATED:
                    return PlayerPlatform.DEDICATED;
                case TVOS:
                    return PlayerPlatform.TVOS;
                case PS4:
                    return PlayerPlatform.PLAYSTATION;
                case NX:
                    return PlayerPlatform.SWITCH;
                case XBOX:
                    return PlayerPlatform.XBOX;
                case WINDOWS_PHONE:
                    return PlayerPlatform.WINDOWS_PHONE;
                default:
                    return PlayerPlatform.UNKNOWN;
            }
        }
        else return PlayerPlatform.JAVA;
    }
}
