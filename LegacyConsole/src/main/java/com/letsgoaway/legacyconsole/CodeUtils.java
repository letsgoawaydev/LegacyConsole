package com.letsgoaway.legacyconsole;

import org.bukkit.Bukkit;

public class CodeUtils {
    public static void runCommand(String command) {
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }
}
