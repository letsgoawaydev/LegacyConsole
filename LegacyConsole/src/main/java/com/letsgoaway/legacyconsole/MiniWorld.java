package com.letsgoaway.legacyconsole;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MiniWorld {
    public MultiverseWorld world;

    public MiniGame game;
    public ArrayList<Player> players = new ArrayList<Player>();


    MiniWorld(MultiverseWorld world, MiniGame game) {
        this.world = world;
        this.game = game;
        game.miniWorld = this;
        game.create();
        Bukkit.getPluginManager().registerEvents(game, LegacyConsole.plugin);
    }

    public void update() {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (players.contains(player) && (!player.getWorld().equals(world.getCBWorld())))
            {
                players.remove(player);
                game.onPlayerLeave(player);
                continue;
            }
            if (!players.contains(player) && (player.getWorld().equals(world.getCBWorld())))
            {
                players.add(player);
                game.onPlayerJoin(player);
                continue;
            }
        }
        game.update();
    }



    public void transferToMiniWorld(MiniWorld transferWorld) {
        for (Player player : players)
        {
            player.teleport(transferWorld.world.getSpawnLocation());

        }

    }

    public void transferPlayerToMiniWorld(Player player, MiniWorld transferWorld) {
        player.teleport(transferWorld.world.getSpawnLocation());
    }

    public void destroy() {
        WorldUtils.deleteWorld(world.getName());
    }
}
