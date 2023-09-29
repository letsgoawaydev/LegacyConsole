package com.letsgoaway.legacyconsole;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.comphenix.protocol.events.PacketEvent;

public class MiniGame implements Listener {
    public MiniWorld miniWorld;

    public void create() {}

    public void update() {}

    public boolean gameHasPlayer(Player player) {
        if (miniWorld.players.contains(player)) return true;
        else return false;
    }



    // #region PLAYER EVENTS
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {}

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {

    }

    @EventHandler
    public void onAnimation(PlayerAnimationEvent event) {

    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {

    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {


    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

    }

    @EventHandler
    public void onElytraGlide(EntityToggleGlideEvent event) {

    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {

    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

    }

    @EventHandler
    public void onBlockDig(BlockDamageEvent event) {

    }

    @EventHandler
    public void onHotbar(PlayerItemHeldEvent event) {

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {

    }

    @EventHandler
    public void onHangingEntityBreakByEntity(HangingBreakByEntityEvent event) {

    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {

    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {}

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        miniWorld.players.remove(event.getPlayer());
        onPlayerLeave(event.getPlayer());
    }

    public void onPlayerJoin(Player player) {}

    public void onPlayerLeave(Player player) {}


    // #endregion
}
