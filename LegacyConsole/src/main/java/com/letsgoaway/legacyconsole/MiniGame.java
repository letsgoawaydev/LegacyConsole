package com.letsgoaway.legacyconsole;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.listeners.AsyncChatEvent;

import net.md_5.bungee.api.ChatColor;


// NOTE: DONT PUT @EVENTHANDLER ONTOP OF
// EVENTS!!! they are automatically called
// because it checks if the player is actually in
// the game
// also DO NOT REPLACE ANY HANDLERS!!!
public class MiniGame implements Listener {
    public MiniWorld miniWorld;

    public boolean dontSendJoinMessage = false;
    public boolean dontSendLeaveMessage = false;


    public void create() {}

    public void update() {}

    public MultiverseWorld createWorld(String uuidAsString) {
        return WorldUtils.createRuntimeWorld("lobby_backup", "minigame_" + uuidAsString, null);
    }

    public boolean gameHasPlayer(Player player) {
        if (!miniWorld.gameIsDone)
        {
            return miniWorld.players.contains(player) && !miniWorld.gameIsDone;
        }
        else return false;
    }

    public boolean gameHasEntity(Entity ent) {
        if (!miniWorld.gameIsDone)
        {
            return ent.getWorld().equals(miniWorld.world.getCBWorld());

        }
        else return false;
    }

    // #region PLAYER EVENTS
    /* Automatically called by MiniWorld */
    public void handler_onPlayerJoin(Player player) {
        if (!dontSendJoinMessage)
        {
            for (Player players : miniWorld.players)
            {
                players.sendRawMessage(ChatColor.YELLOW + player.getName() + " has joined the game");
            }
        }
        onPlayerJoin(player);
    }

    public void onPlayerJoin(Player player) {}


    public void onPlayerLeave(Player player) {}


    public void handler_onPlayerQuit(Player player) {
        miniWorld.players.remove(player);
        if (!dontSendLeaveMessage)
        {
            for (Player players : miniWorld.players)
            {
                players.sendRawMessage(ChatColor.YELLOW + player.getName() + " has left the game");
            }
        }
        onPlayerLeave(player);
    }

    @EventHandler
    public void handler_onPlayerQuitEvent(PlayerQuitEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            if (!dontSendLeaveMessage)
            {
                miniWorld.players.remove(event.getPlayer());
                for (Player players : miniWorld.players)
                {
                    players.sendRawMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has left the game");
                }
            }
            onPlayerLeave(event.getPlayer());
        }
    }

    @EventHandler
    public void handler_onPlayerDisconnect(PlayerKickEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            if (!dontSendLeaveMessage)
            {
                miniWorld.players.remove(event.getPlayer());
                for (Player players : miniWorld.players)
                {
                    players.sendRawMessage(ChatColor.YELLOW + event.getPlayer().getName() + " has been kicked from the game");
                }
            }
            onPlayerLeave(event.getPlayer());
        }
    }

    @EventHandler
    public void handler_onPlayerChat(AsyncPlayerChatEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerChat(event);
            if (!event.isCancelled())
            {
                for (Player player : miniWorld.players)
                {
                    player.sendRawMessage("<" + event.getPlayer().getName() + "> " + event.getMessage());
                    event.setCancelled(true);
                }
            }
        }
    }

    public void onPlayerChat(AsyncPlayerChatEvent event) {}


    @EventHandler
    public void handler_onPlayerMove(PlayerMoveEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerMove(event);
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {}

    @EventHandler
    public void handler_onPlayerOffhandSwitch(PlayerSwapHandItemsEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerOffhandSwitch(event);
        }
    }

    public void onPlayerOffhandSwitch(PlayerSwapHandItemsEvent event) {}



    @EventHandler
    public void handler_onPlayerDeath(PlayerDeathEvent event) {
        if (gameHasPlayer(event.getEntity()))
        {
            onPlayerDeath(event);
        }
    }

    public void onPlayerDeath(PlayerDeathEvent event) {}



    @EventHandler
    public void handler_onPlayerAnimation(PlayerAnimationEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerAnimation(event);
        }
    }

    public void onPlayerAnimation(PlayerAnimationEvent event) {}



    @EventHandler
    public void handler_onPlayerInteract(PlayerInteractEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerInteract(event);
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {}



    @EventHandler
    public void handler_onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerInteractEntity(event);
        }
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {}



    @EventHandler
    public void handler_onPlayerSneak(PlayerToggleSneakEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerSneak(event);
        }
    }

    public void onPlayerSneak(PlayerToggleSneakEvent event) {}



    @EventHandler
    public void handler_onPlayerDropItem(PlayerDropItemEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerDropItem(event);
        }
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {}



    @EventHandler
    public void handler_onPlayerBlockBreak(BlockBreakEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerBlockBreak(event);
        }
    }

    public void onPlayerBlockBreak(BlockBreakEvent event) {}



    @EventHandler
    public void handler_onPlayerBlockDig(BlockDamageEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerBlockDig(event);
        }
    }

    public void onPlayerBlockDig(BlockDamageEvent event) {}



    @EventHandler
    public void handler_onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerResourcePackStatus(event);
        }
    }

    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {}



    @EventHandler
    public void handler_onPlayerHotbarSwitch(PlayerItemHeldEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            onPlayerHotbarSwitch(event);
        }
    }

    public void onPlayerHotbarSwitch(PlayerItemHeldEvent event) {}



    @EventHandler
    public void handler_onPlayerInventoryOpen(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player)
        {
            if (gameHasPlayer((Player) event.getPlayer()))
            {
                onPlayerInventoryOpen(event);
            }
        }
    }

    /*
     * NOTE FOR INVENTORY EVENTS
     * 
     * event.getPlayer() returns a HumanEntity instead of the Player class, however
     * i have made it so it will only call if event.getPlayer() is an instance of
     * Player, so you should be able to cast to Player without any issues
     */
    public void onPlayerInventoryOpen(InventoryOpenEvent event) {}



    @EventHandler
    public void handler_onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player)
        {
            if (gameHasPlayer((Player) event.getPlayer()))
            {
                onPlayerInventoryClose(event);
            }
        }
    }

    /*
     * NOTE FOR INVENTORY EVENTS
     * 
     * event.getPlayer() returns a HumanEntity instead of the Player class, however
     * i have made it so it will only call if event.getPlayer() is an instance of
     * Player, so you should be able to cast to Player without any issues
     */
    public void onPlayerInventoryClose(InventoryCloseEvent event) {}



    @EventHandler
    public void handler_onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player)
        {
            if (gameHasPlayer((Player) event.getWhoClicked()))
            {
                onPlayerInventoryClick(event);
            }
        }
    }

    /*
     * NOTE FOR INVENTORY EVENTS
     * 
     * event.getWhoClicked() returns a HumanEntity instead of the Player class,
     * however i have made it so it will only call if event.getPlayer() is an
     * instance of Player, so you should be able to cast to Player without any
     * issues
     */
    public void onPlayerInventoryClick(InventoryClickEvent event) {}

    @EventHandler
    public void handler_onPlayerInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player)
        {
            if (gameHasPlayer((Player) event.getWhoClicked()))
            {
                onPlayerInventoryDrag(event);
            }
        }
    }

    /*
     * NOTE FOR INVENTORY EVENTS
     * 
     * event.getWhoClicked() returns a HumanEntity instead of the Player class,
     * however i have made it so it will only call if event.getPlayer() is an
     * instance of Player, so you should be able to cast to Player without any
     * issues
     */
    public void onPlayerInventoryDrag(InventoryDragEvent event) {}


    // #endregion

    // #region ENTITY EVENTS
    @EventHandler
    public void handler_onEntityDamage(EntityDamageEvent event) {
        if (gameHasEntity(event.getEntity()))
        {
            onEntityDamage(event);
        }
    }

    public void onEntityDamage(EntityDamageEvent event) {}



    @EventHandler
    public void handler_onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (gameHasEntity(event.getEntity()) || gameHasEntity(event.getDamager()))
        {
            onEntityDamageByEntity(event);
        }
    }

    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {}



    @EventHandler
    public void handler_onEntityElytraGlide(EntityToggleGlideEvent event) {
        if (gameHasEntity(event.getEntity()))
        {
            onEntityElytraGlide(event);
        }
    }

    public void onEntityElytraGlide(EntityToggleGlideEvent event) {}



    @EventHandler
    public void handler_onEntityItemPickup(EntityPickupItemEvent event) {
        if (gameHasEntity(event.getEntity()))
        {
            onEntityItemPickup(event);
        }
    }

    public void onEntityItemPickup(EntityPickupItemEvent event) {}

    // #endregion

    // #region TYPED ENTITY EVENTS
    @EventHandler
    public void handler_onHangingEntityBreakByEntity(HangingBreakByEntityEvent event) {
        if (gameHasEntity(event.getEntity()))
        {
            onHangingEntityBreakByEntity(event);
        }
    }

    public void onHangingEntityBreakByEntity(HangingBreakByEntityEvent event) {}



    @EventHandler
    public void handler_onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (gameHasPlayer(event.getPlayer()) || gameHasEntity(event.getRightClicked()))
        {
            onArmorStandManipulate(event);
        }
    }

    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {}
    // #endregion
}
