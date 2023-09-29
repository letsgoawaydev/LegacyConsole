package com.letsgoaway.legacyconsole;

import org.bukkit.attribute.Attribute;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.comphenix.protocol.events.PacketEvent;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public final class PlayerListener implements Listener {
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {

	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {}


	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.getPlayer().getInventory().clear();
		for (MiniWorld hub : LegacyConsole.hubs.values())
		{
			if (hub.world.getCBWorld().getPlayers().size() >= 20) continue;
			else
			{
				event.getPlayer().teleport(hub.world.getSpawnLocation());
				return;
			}
		}
		event.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
		event.getPlayer().kickPlayer("All hubs are currently full! Try again later.");
	}

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

	public static void onFootstep(PacketEvent event, Player player) {

	}


}
