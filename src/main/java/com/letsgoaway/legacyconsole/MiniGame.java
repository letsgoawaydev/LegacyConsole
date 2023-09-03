package com.letsgoaway.legacyconsole;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.comphenix.protocol.events.PacketEvent;

public class MiniGame {
    public static void create() {}

    public static void update() {}

    public static void onLogin(PlayerLoginEvent event) {}

    public static void onPlayerDeath(PlayerDeathEvent event) {}

    public static void onQuit(PlayerQuitEvent event) {}

    public static void onJoin(PlayerJoinEvent event) {}

    public static void onAnimation(PlayerAnimationEvent event) {}

    public static void onInteract(PlayerInteractEvent event) {}

    public static void onInteractEntity(PlayerInteractEntityEvent event) {}

    public static void onSneak(PlayerToggleSneakEvent event) {}

    public static void onEntityDamage(EntityDamageEvent event) {}

    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {}

    public static void onElytraGlide(EntityToggleGlideEvent event) {}

    public static void onItemPickup(EntityPickupItemEvent event) {}

    public static void onItemDrop(PlayerDropItemEvent event) {}

    public static void onBlockBreak(BlockBreakEvent event) {}

    public static void onBlockDig(BlockDamageEvent event) {}

    public static void onFootstep(PacketEvent event, Player player) {}
}
