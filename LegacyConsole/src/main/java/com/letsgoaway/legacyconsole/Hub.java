package com.letsgoaway.legacyconsole;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;

public class Hub extends MiniGame {
    Boat tpBoat;
    Minecart tpMinecart;


    @Override
    public void create() {
        super.create();
        WorldUtils.worldManager.setFirstSpawnWorld(miniWorld.world.getName());
        tpBoat = (Boat) miniWorld.world.getCBWorld().spawnEntity(new Location(miniWorld.world.getCBWorld(), -102, 95, -12, 0, 0), EntityType.BOAT);
        tpBoat.setSilent(true);
        tpBoat.setInvulnerable(true);
        tpMinecart = (Minecart) miniWorld.world.getCBWorld().spawnEntity(new Location(miniWorld.world.getCBWorld(), -102, 95, -12, 0, 0), EntityType.MINECART);
        tpMinecart.setSilent(true);
        tpMinecart.setInvulnerable(true);
        miniWorld.dontAutoDelete = true;
        dontSendJoinMessage = true;
        dontSendLeaveMessage = true;

    }

    public HashMap<Player, Boolean> playersHaveInvOpen = new HashMap<Player, Boolean>();

    @Override
    public void update() {
        for (Player player : miniWorld.players)
        {

            if (!Editions.isOnBedrock(player))
            {
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(tpBoat);
            }
            else
            {
                tpMinecart.addPassenger(player);

                // player.teleport(new Location(miniWorld.world.getCBWorld(), -102, 95, -12,
                // tpBoat.getLocation().getYaw() + 0.2f, 0));

            }
            tpBoat.teleport(new Location(miniWorld.world.getCBWorld(), -102, 95, -12, tpBoat.getLocation().getYaw() + 0.2f, 0));
            tpMinecart.teleport(new Location(miniWorld.world.getCBWorld(), -102, 95, -12, tpBoat.getLocation().getYaw(), 0));
            player.setFallDistance(0);
            player.setFireTicks(0);
            player.setAllowFlight(true);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255, true, false), true);
            player.setHealth(20);
            player.setSaturation(99999);
            player.setFoodLevel(24);

        }
        miniWorld.world.setEnableWeather(false);

        super.update();
    }

    @Override
    public MultiverseWorld createWorld(String uuidAsString) {
        return WorldUtils.createRuntimeWorld("tut_hub", "hub_" + uuidAsString, null);
    }

    @Override
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (Editions.isOnJava(event.getPlayer())) event.getPlayer().setSpectatorTarget(tpBoat);
    }

    @Override
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onPlayerBlockDig(BlockDamageEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
        {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        SGMenu myAwesomeMenu;
        if (Editions.isOnBedrock(event.getPlayer()))
        {
            myAwesomeMenu = LegacyConsole.spiGUI.create("Menu", 3);
        }
        else
        {
            myAwesomeMenu = LegacyConsole.spiGUI.create("Menu", 1);
        }
        ItemStack paper = new ItemStack(Material.PAPER, 1);
        ItemMeta itemmeta = paper.getItemMeta();
        itemmeta.setCustomModelData(1);
        paper.setItemMeta(itemmeta);
        ItemBuilder battleIcon = new ItemBuilder(paper);
        battleIcon.name("&l&r&fBattle");
        SGButton readyButton = new SGButton(battleIcon.build()).withListener((InventoryClickEvent event2) -> {
            boolean transferedPlayer = false;
            if (LegacyConsole.battle_lobby.size() != 0)
            {
                for (MiniWorld world : LegacyConsole.battle_lobby)
                {
                    if (!(world.players.size() <= 16))
                    {
                        continue;
                    }
                    else
                    {
                        transferedPlayer = true;
                        miniWorld.transferPlayerToMiniWorld((Player) event2.getWhoClicked(), world);
                    }
                }
            }
            if (!transferedPlayer)
            {
                String randomName = "battlelobby_" + UUID.randomUUID().toString().replace("-", "_");
                MiniWorld world = new MiniWorld(new Lobby(GameTypes.BATTLE));
                LegacyConsole.battle_lobby.add(world);
                miniWorld.transferPlayerToMiniWorld((Player) event2.getWhoClicked(), world);
            }
        });
        myAwesomeMenu.setButton(0, readyButton);
        event.getPlayer().openInventory(myAwesomeMenu.getInventory());
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent event) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
            public void run() {
                event.getEntity().spigot().respawn();
            }
        }, 20L);
    }

    @Override
    public void onPlayerInventoryClose(InventoryCloseEvent event) {}

    @Override
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (!Editions.isOnBedrock(event.getPlayer()))
        {
            if (event.getStatus().equals(Status.SUCCESSFULLY_LOADED))
            {

                Sounds.playForPlayer(Sounds.MENU_MUSIC, event.getPlayer(), event.getPlayer().getLocation());
            }
            else if (event.getStatus().equals(Status.DECLINED))
            {
                event.getPlayer().kickPlayer("Please accept the resource pack!");
            }
            else if (event.getStatus().equals(Status.FAILED_DOWNLOAD))
            {
                event.getPlayer().kickPlayer("Pack failed to download. Please rejoin and try again.");
            }
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        if (!Editions.isOnBedrock(player))
        {
            player.setGameMode(GameMode.SPECTATOR);
            playersHaveInvOpen.put(player, false);
        }
        else
        {
            tpMinecart.addPassenger(player);
            Sounds.playForPlayer(Sounds.MENU_MUSIC, player, player.getLocation());
        }
    }
}
