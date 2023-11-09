package com.letsgoaway.legacyconsole.commands;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.letsgoaway.legacyconsole.LegacyConsole;
import com.letsgoaway.legacyconsole.MiniWorld;

public class HubCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            player.getInventory().clear();
            for (MiniWorld hub : LegacyConsole.hubs)
            {
                if (hub.world.getCBWorld().getPlayers().size() >= 20) continue;
                else
                {
                    player.teleport(hub.world.getSpawnLocation());
                    return true;
                }
            }
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            player.kickPlayer("All hubs are currently full! Try again later.");
        }
        return false;
    }
}