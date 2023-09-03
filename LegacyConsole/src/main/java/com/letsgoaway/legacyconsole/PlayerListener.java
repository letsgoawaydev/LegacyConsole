package com.letsgoaway.legacyconsole;

import java.util.Arrays;
import java.util.List;

import javax.swing.Timer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.World;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.comphenix.protocol.events.PacketEvent;

public final class PlayerListener implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onLogin(event);
            case TUMBLE:
                Tumble.onLogin(event);
            case GLIDE:
                Glide.onLogin(event);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onPlayerDeath(event);
            case TUMBLE:
                Tumble.onPlayerDeath(event);
            case GLIDE:
                Glide.onPlayerDeath(event);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onQuit(event);
            case TUMBLE:
                Tumble.onQuit(event);
            case GLIDE:
                Glide.onQuit(event);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onJoin(event);
            case TUMBLE:
                Tumble.onJoin(event);
            case GLIDE:
                Glide.onJoin(event);
        }
    }

    @EventHandler
    public void onAnimation(PlayerAnimationEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onAnimation(event);
            case TUMBLE:
                Tumble.onAnimation(event);
            case GLIDE:
                Glide.onAnimation(event);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onInteract(event);
            case TUMBLE:
                Tumble.onInteract(event);
            case GLIDE:
                Glide.onInteract(event);
        }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onInteractEntity(event);
            case TUMBLE:
                Tumble.onInteractEntity(event);
            case GLIDE:
                Glide.onInteractEntity(event);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onSneak(event);
            case TUMBLE:
                Tumble.onSneak(event);
            case GLIDE:
                Glide.onSneak(event);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onEntityDamage(event);
            case TUMBLE:
                Tumble.onEntityDamage(event);
            case GLIDE:
                Glide.onEntityDamage(event);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onEntityDamageByEntity(event);
            case TUMBLE:
                Tumble.onEntityDamageByEntity(event);
            case GLIDE:
                Glide.onEntityDamageByEntity(event);
        }
    }

    @EventHandler
    public void onElytraGlide(EntityToggleGlideEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onElytraGlide(event);
            case TUMBLE:
                Tumble.onElytraGlide(event);
            case GLIDE:
                Glide.onElytraGlide(event);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onItemPickup(event);
            case TUMBLE:
                Tumble.onItemPickup(event);
            case GLIDE:
                Glide.onItemPickup(event);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onItemDrop(event);
            case TUMBLE:
                Tumble.onItemDrop(event);
            case GLIDE:
                Glide.onItemDrop(event);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onBlockBreak(event);
            case TUMBLE:
                Tumble.onBlockBreak(event);
            case GLIDE:
                Glide.onBlockBreak(event);
        }
    }

    @EventHandler
    public void onBlockDig(BlockDamageEvent event) {
        switch (App.mode) {
            case BATTLE:
                Battle.onBlockDig(event);
            case TUMBLE:
                Tumble.onBlockDig(event);
            case GLIDE:
                Glide.onBlockDig(event);
        }
    }

    public static void onFootstep(PacketEvent event, Player player) {
        switch (App.mode) {
            case BATTLE:
                Battle.onFootstep(event, player);
            case TUMBLE:
                Tumble.onFootstep(event, player);
            case GLIDE:
                Glide.onFootstep(event, player);
        }
    }
}
