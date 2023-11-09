package com.letsgoaway.legacyconsole.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.letsgoaway.legacyconsole.resourcepack.ResourcePackServer;

public class SetResourceProxyCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            if (player.isOp())
            {
                ResourcePackServer.url = args[0];
            }

        }
        return false;
    }
}