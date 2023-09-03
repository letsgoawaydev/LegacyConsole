package com.letsgoaway.legacyconsole;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.NoteBlock;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.events.PacketEvent;

public class Battle extends MiniGame {
    static BossBar bossBar;
    static int amountInSpectator = 0;
    static ArrayList<GameMode> spectatorGameMode = new ArrayList<GameMode>();

    public static void create() {
	spectatorGameMode.add(GameMode.SPECTATOR);
	spectatorGameMode.add(GameMode.SURVIVAL);
    }

    public static void update() {
	if (bossBar == null)
	{
	    bossBar = App.plugin.getServer().createBossBar("", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
	}
	amountInSpectator = 0;
	if ((Math.round(App.tickCounter * 0.6f) % 21) == 0)
	{
	    Sounds.playForAll(Sounds.CHEST, Bukkit.getWorld("lobby"), null);
	}
	for (Player player : App.plugin.getServer().getOnlinePlayers())
	{
	    player.getWorld().setGameRuleValue("doMobSpawning", "false");
	    if (!player.isDead() && !isSpectator(player))
	    {
		player.setGameMode(GameMode.ADVENTURE);
	    }
	    else
		if (!isSpectator(player))
		{
		    player.setGameMode(GameMode.SURVIVAL);
		    bossBar.addPlayer(player);
		}
	    if (isSpectator(player))
	    {
		amountInSpectator++;
		for (Entity entity : player.getWorld().getEntities())
		{
		    if (entity.getCustomName() != null)
		    {
			if (entity.getCustomName().equals(player.getDisplayName()) && entity.getType().equals(EntityType.BAT))
			{
			    Bat playerBat = (Bat) entity;
			    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			    player.setSaturation(999);
			    player.setFoodLevel(20);
			    player.setInvulnerable(true);
			    player.setFireTicks(0);
			    player.setFallDistance(0);
			    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 255, false, false), true);
			    // this is crap
			    player.addPassenger(playerBat);
			    playerBat.teleport(player.getEyeLocation());
			    player.addPassenger(playerBat);
			    player.setAllowFlight(true);
			    player.getInventory().clear();
			    ItemStack anvilSqueak = new ItemStack(Material.ANVIL);
			    player.getInventory().setItem(0, anvilSqueak);
			    // player.hidePlayer(App.plugin, player);
			    player.setFlying(true);
			    playerBat.setAI(false);
			    playerBat.setAwake(true);
			    // playerBat.teleport(playerBat.getLocation().add(-0.5, 0.5, 0));
			    playerBat.setInvulnerable(true);
			    playerBat.setSilent(true);
			    playerBat.setGlowing(false);
			    playerBat.setHealth(6);
			    playerBat.setCollidable(false);
			    playerBat.setRemoveWhenFarAway(false);
			    playerBat.setFireTicks(0);
			    // player.addPotionEffect(
			    // new PotionEffect(PotionEffectType.INVISIBILITY, 65535, 255, false, true));
			}
		    }
		}
	    }
	    else
	    {
		player.getWorld().setGameRuleValue("dofiretick", "false");
		if (bossBar.getPlayers().contains(player))
		{
		    bossBar.removePlayer(player);
		}
		// if we are in pvp mode
		for (Entity entity : player.getWorld().getEntities())
		{
		    if (entity.getCustomName() != null)
		    {
			if (entity.getCustomName().equals(player.getDisplayName()) && entity.getType().equals(EntityType.BAT))
			{
			    entity.remove();
			    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			    player.removePotionEffect(PotionEffectType.INVISIBILITY);
			    player.setFireTicks(0);
			    player.setSaturation(1);
			    player.setFoodLevel(20);
			    player.setGliding(false);
			    player.getInventory().clear();
			    // player.showPlayer(App.plugin, player);
			}
		    }
		}
		player.setInvulnerable(false);
		player.setAllowFlight(false);
		player.setFlying(false);
	    }
	    player.sendTitle("", TextAnims.getFrame(TextAnims.chestRefill, Math.round(App.tickCounter * 0.6f)), 0, 20, 0);
	}
	// bossBar.setTitle(App.plugin.getServer().getOnlinePlayers().size() -
	// amountInSpectator + " players remaining");
	MiniGame.update();
    }

    private static boolean isSpectator(Player player) {
	return spectatorGameMode.contains(player.getGameMode());
    }

    public static void onLogin(PlayerLoginEvent event) {}

    public static void onPlayerDeath(PlayerDeathEvent event) {
	Player player = event.getEntity();
	Sounds.playForAll(Sounds.DEATH, player.getWorld(), null);
	player.getWorld().getBlockAt(162, 64, -194).setType(Material.BED_BLOCK);
	player.setBedSpawnLocation(new Location(player.getWorld(), 162, 64, -194));
	Bukkit.getScheduler().scheduleSyncDelayedTask(App.plugin, new Runnable() {
	    public void run() {
		Bat playerBat = (Bat) player.getWorld().spawnEntity(player.getLocation(), EntityType.BAT);
		playerBat.setCustomName(player.getDisplayName());
		playerBat.setCustomNameVisible(true);
		player.spigot().respawn();
		player.setGameMode(GameMode.SURVIVAL);
		player.teleport(player.getWorld().getSpawnLocation());
	    }
	}, 20L);
    }

    public static void onQuit(PlayerQuitEvent event) {
	String playerName = event.getPlayer().getName();
	World playerWorld = event.getPlayer().getWorld();
	for (Entity entity : playerWorld.getEntities())
	{
	    if (entity.getCustomName() != null)
	    {
		if (entity.getCustomName().equals(playerName) && entity.getType().equals(EntityType.BAT))
		{
		    entity.remove();
		}
	    }
	}
    }

    public static void onJoin(PlayerJoinEvent event) {}

    public static void onAnimation(PlayerAnimationEvent event) {
	Material blockType = event.getPlayer().getTargetBlock(null, 5).getType();
	if (blockType.equals(Material.NOTE_BLOCK) && event.getPlayer().getGameMode().equals(GameMode.ADVENTURE))
	{
	    NoteBlock noteBlock = (NoteBlock) event.getPlayer().getTargetBlock(null, 5).getState();
	    noteBlock.play();
	}
    }

    public static void onInteract(PlayerInteractEvent event) {
	if (isSpectator(event.getPlayer()) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
	{
	    Sounds.playForAll(Sounds.SQUEAK, event.getPlayer().getWorld(), event.getPlayer().getLocation());
	    event.setCancelled(true);
	}
    }

    public static void onInteractEntity(PlayerInteractEntityEvent event) {
	if (isSpectator(event.getPlayer()) && event.getRightClicked().getType().equals(EntityType.BAT) && event.getRightClicked().getCustomName().equals(event.getPlayer().getName()))
	{
	    Sounds.playForAll(Sounds.SQUEAK, event.getPlayer().getWorld(), event.getPlayer().getLocation());
	}
    }

    public static void onSneak(PlayerToggleSneakEvent event) {
	if (event.isSneaking())
	{
	    // Sounds.playForAll(Sounds.CHEST, event.getPlayer().getWorld());
	}
    }

    public static void onEntityDamage(EntityDamageEvent event) {
	if (event.getEntity() instanceof ItemFrame)
	{
	    event.setCancelled(true);
	}
    }

    public static void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
	if (event.getDamager() instanceof Player)
	{
	    Player attacker = (Player) event.getDamager();
	    if (isSpectator(attacker))
	    {
		attacker.setSpectatorTarget(attacker);
		attacker.setSpectatorTarget(attacker);
		event.setCancelled(true);
	    }
	}
    }

    public static void onElytraGlide(EntityToggleGlideEvent event) {
	if (event.getEntity() instanceof Player)
	{
	    Player player = (Player) event.getEntity();
	    if (isSpectator(player))
	    {
		event.setCancelled(true);
	    }
	}
    }

    public static void onItemPickup(EntityPickupItemEvent event) {
	if (event.getEntity() instanceof Player)
	{
	    Player player = (Player) event.getEntity();
	    if (isSpectator(player))
	    {
		event.setCancelled(true);
	    }
	}
    }

    public static void onItemDrop(PlayerDropItemEvent event) {
	if (isSpectator(event.getPlayer()))
	{
	    event.setCancelled(true);
	}
    }

    public static void onBlockBreak(BlockBreakEvent event) {
	if (isSpectator(event.getPlayer()))
	{
	    event.setCancelled(true);
	}
    }

    public static void onBlockDig(BlockDamageEvent event) {
	if (isSpectator(event.getPlayer()))
	{
	    event.setCancelled(true);
	}
    }

    public static void onFootstep(PacketEvent event, Player player) {
	if (isSpectator(player))
	{
	    event.setCancelled(true);
	}
    }
}
