package com.letsgoaway.legacyconsole;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;

public class Hub extends MiniGame {
    @Override
    public void create() {
        super.create();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(LegacyConsole.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (gameHasPlayer(event.getPlayer()) && event.getPacket().getPlayerDigTypes().read(0).equals(PlayerDigType.DROP_ITEM))
                {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
                        public void run() {
                            miniWorld.transferPlayerToMiniWorld(event.getPlayer(), LegacyConsole.battle_lobby.get("temp"));
                        }
                    });

                }
            }
        });
    }

    public HashMap<Player, Boolean> playersHaveInvOpen = new HashMap<Player, Boolean>();

    @Override
    public void update() {
        for (Player player : miniWorld.players)
        {
            if (!player.isDead())
            {
                player.setFallDistance(0);
                player.setFireTicks(0);
                player.setAllowFlight(true);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 999999, 255, true, false, null), true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255, true, false, null), true);

                player.setHealth(2);
                player.setNoDamageTicks(999);
                player.setGameMode(GameMode.ADVENTURE);
                player.setSaturation(99999);
                player.setFoodLevel(24);
                player.teleport(new Location(miniWorld.world.getCBWorld(), -102, 95, -12, player.getLocation().getYaw() + 0.1f, 0), TeleportCause.ENDER_PEARL);

                player.hidePlayer(LegacyConsole.plugin, player);
            }


        }
        miniWorld.world.setEnableWeather(false);

        super.update();
    }

    @EventHandler
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @Override
    public void onBlockDig(BlockDamageEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
        {
            Player player = (Player) event.getDamager();
            if (gameHasPlayer(player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    @Override
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (gameHasPlayer(event.getEntity()))
        {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
                public void run() {
                    event.getEntity().spigot().respawn();
                }
            }, 20L);
        }
    }

    @EventHandler
    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

    }

    @EventHandler
    @Override
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            Sounds.playForPlayer(Sounds.MENU_MUSIC, event.getPlayer(), event.getPlayer().getLocation());
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (gameHasPlayer(player))
        {
            playersHaveInvOpen.put(player, false);
            Sounds.playForPlayer(Sounds.MENU_MUSIC, player, player.getLocation());
        }
    }
}
