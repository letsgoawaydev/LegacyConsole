package com.letsgoaway.legacyconsole;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.InventoryView.Property;

import com.comphenix.protocol.events.PacketEvent;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public final class MiscListener implements Listener {
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
		event.getPlayer().getInventory().clear();
		for (MiniWorld hub : LegacyConsole.hubs)
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
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		LegacyConsole.resourcePackServer.sendPack(event.getPlayer(), "battle");
		event.getPlayer().getInventory().clear();
		for (MiniWorld hub : LegacyConsole.hubs)
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
	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void worldInit(org.bukkit.event.world.WorldInitEvent e) {
		e.getWorld().setKeepSpawnInMemory(false);
		e.getWorld().setAutoSave(false);
	}
}
