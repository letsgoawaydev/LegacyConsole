package com.letsgoaway.legacyconsole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class MiniWorld {
    public MultiverseWorld world;
    private boolean gameStarted = false;
    public boolean gameIsDone = false;
    private int timeWithOutPlayers = 0;
    public boolean dontAutoDelete = false;
    public MiniGame game;
    public ArrayList<Player> players = new ArrayList<Player>();

    MiniWorld(MiniGame game) {
        gameIsDone = false;
        gameStarted = false;
        this.world = game.createWorld(UUID.randomUUID().toString().replace("-", "_"));
        this.game = game;
        game.miniWorld = this;
        game.create();
        Bukkit.getPluginManager().registerEvents(game, LegacyConsole.plugin);

    }

    public void update() {
        if (!gameIsDone)
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (players.contains(player) && (!player.getWorld().equals(world.getCBWorld())))
                {
                    players.remove(player);
                    game.handler_onPlayerQuit(player);
                    continue;
                }
                if (!players.contains(player) && (player.getWorld().equals(world.getCBWorld())))
                {
                    players.add(player);
                    player.teleport(world.getSpawnLocation());
                    game.handler_onPlayerJoin(player);
                    continue;
                }
            }
            // code that deletes the world if inactive
            if (!dontAutoDelete)
            {
                if (players.size() > 0)
                {
                    gameStarted = true;
                }
                if (gameStarted && players.size() == 0)
                {
                    destroy();
                }
                if (!gameStarted)
                {
                    timeWithOutPlayers++;
                }
                if (timeWithOutPlayers >= 1200)
                {
                    destroy();
                }

            }
            if (game != null)
            {
                game.update();
            }
        }
    }



    public void transferToMiniWorld(MiniWorld transferWorld) {
        for (Player player : players)
        {
            player.teleport(transferWorld.world.getSpawnLocation());
            player.setGravity(true);
            player.setAllowFlight(false);
            player.setFlying(false);
            player.setFallDistance(0);
            player.setFireTicks(0);
            player.setAllowFlight(false);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
            for (PotionEffect effect : player.getActivePotionEffects())
            {
                player.removePotionEffect(effect.getType());
            }
            player.setHealth(20);
            player.setNoDamageTicks(0);
            player.setGameMode(GameMode.ADVENTURE);
            player.setSaturation(5);
            player.setFoodLevel(20);
            player.setInvulnerable(false);
            player.getInventory().clear();
        }
    }

    public void transferPlayerToMiniWorld(Player player, MiniWorld transferWorld) {
        player.setWalkSpeed(0.2f);
        player.teleport(transferWorld.world.getSpawnLocation());
        player.setGravity(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setAllowFlight(false);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
        for (PotionEffect effect : player.getActivePotionEffects())
        {
            player.removePotionEffect(effect.getType());
        }
        player.setHealth(20);
        player.setNoDamageTicks(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setSaturation(5);
        player.setFoodLevel(20);
        player.setInvulnerable(false);
        player.getInventory().clear();
    }

    public void destroy() {
        gameIsDone = true;
        WorldUtils.deleteWorld(world.getName());
        world = null;
        game = null;
    }
}
