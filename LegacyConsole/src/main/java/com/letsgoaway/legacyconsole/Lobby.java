package com.letsgoaway.legacyconsole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.geysermc.cumulus.Form;
import org.geysermc.floodgate.api.FloodgateApi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Lobby extends MiniGame {
    FloodgateApi api;
    BossBar spacerBar;
    BossBar infoBar;
    GameTypes type;
    HashMap<String, String> maps = new HashMap<String, String>();
    HashMap<Player, String> mapVotes = new HashMap<Player, String>();
    HashMap<String, Integer> mapVoteNums = new HashMap<String, Integer>();
    HashMap<Player, Boolean> dontPlayBackSound = new HashMap<Player, Boolean>();

    public Lobby(GameTypes type) {
        super();
        this.type = type;
    }

    @Override
    public MultiverseWorld createWorld(String newName) {
        return WorldUtils.createRuntimeWorld("lobby_backup", "lobby_" + newName, null);
    }

    public TickTimer timerGameChange;

    public void startGamemode() {

        dontSendLeaveMessage = true;
        if (type.equals(GameTypes.BATTLE))
        {
            int lastHighest = 0;
            String lastHighestMapName = "";
            for (String map : mapVoteNums.keySet())
            {
                if (mapVoteNums.get(map) >= lastHighest)
                {
                    lastHighest = mapVoteNums.get(map);
                    lastHighestMapName = map;
                }
            }
            if (lastHighestMapName.equals(""))
            {
                // please tell me there is an easier way for this god awful thing
                ArrayList<String> mapNames = new ArrayList<String>();
                for (String map : mapVoteNums.keySet())
                {
                    mapNames.add(map);
                }
                lastHighestMapName = mapNames.get((int) Math.round(Math.floor(Math.random() * mapNames.size() - 1)));
            }
            String mapName = maps.get(lastHighestMapName);
            if (miniWorld.players.size() <= 8)
            {
                mapName += "_s";
            }
            else
            {
                mapName += "_l";
            }
            MiniWorld battleWorld = new MiniWorld(new Battle(mapName, miniWorld.players.size()));
            LegacyConsole.battle.add(battleWorld);
            miniWorld.transferToMiniWorld(battleWorld);
        }

    }

    public void create() {
        timerGameChange = new TickTimer(60F, new Runnable() {
            public void run() {
                startGamemode();
            }
        }, new Runnable() {
            public void run() {
                onTimerGameChangeSecond();
            }
        });
        timerGameChange.finishOnRoundedZero = true;
        if (type.equals(GameTypes.BATTLE))
        {
            maps.put("Cove", "cove");
            maps.put("Crucible", "crucible");
            maps.put("Cavern", "cavern");
        }
        for (String map : maps.keySet())
        {
            mapVoteNums.put(map, 0);
        }
        spacerBar = Bukkit.createBossBar(" ", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        infoBar = Bukkit.createBossBar("1 or more additional players are required to start the round...", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        api = FloodgateApi.getInstance();
        miniWorld.world.setPVPMode(false);
        super.create();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(LegacyConsole.plugin, ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (gameHasPlayer(event.getPlayer())
                        && (event.getPacket().getPlayerDigTypes().read(0).equals(PlayerDigType.DROP_ALL_ITEMS) || event.getPacket().getPlayerDigTypes().read(0).equals(PlayerDigType.DROP_ITEM))
                        && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR) && event.getPlayer().isSneaking())
                {

                    createGUIMenu(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        });
    }

    @Override
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().equals("start"))
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
                public void run() {
                    startGamemode();
                }
            });

        }
    }

    public void onTimerGameChangeSecond() {
        int playersReady = 0;
        for (boolean isPlayerReady : readyMap.values())
        {
            if (isPlayerReady)
            {
                playersReady++;
            }
        }
        if (playersReady == readyMap.values().size())
        {
            startGamemode();
        }
        if (timerGameChange.roundedSecondsLeft == 5 || timerGameChange.roundedSecondsLeft == 3)
        {
            for (Player player : miniWorld.players)
            {
                Sounds.playForPlayer(Sounds.LOBBY, player, player.getLocation());
            }
        }
        else if (timerGameChange.roundedSecondsLeft == 4 || timerGameChange.roundedSecondsLeft == 2)
        {
            for (Player player : miniWorld.players)
            {
                Sounds.playForPlayer(Sounds.LOBBYACCENT, player, player.getLocation());
            }
        }
        else if (timerGameChange.roundedSecondsLeft == 1)
        {
            for (Player player : miniWorld.players)
            {
                Sounds.playForPlayer(Sounds.LOBBYZERO, player, player.getLocation());
            }
        }
    }

    public void updateMapVoteNums() {
        for (String map : mapVoteNums.keySet())
        {
            mapVoteNums.replace(map, 0);
        }
        for (String map : mapVotes.values())
        {
            if (mapVoteNums.containsKey(map))
            {

                mapVoteNums.replace(map, mapVoteNums.get(map) + 1);
            }
        }
    }

    public HashMap<Player, Boolean> readyMap = new HashMap<Player, Boolean>();



    @Override
    public void update() {
        if (miniWorld.players.size() == 1)
        {
            infoBar.setTitle("1 or more additional players are required to start the round...");
            timerGameChange.stop();
        }
        else
        {
            infoBar.setTitle("Time to game change: " + timerGameChange.roundedSecondsLeft + " seconds");
        }
        updateMapVoteNums();
        for (Player player : miniWorld.players)
        {

            if (!readyMap.get(player))
            {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Not Ready"));
            }
            else
            {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Ready"));
            }
            if (!player.isDead())
            {

                player.setGravity(true);
                player.setAllowFlight(false);
                player.setFlying(false);
                player.setFallDistance(0);
                player.setFireTicks(0);
                player.setAllowFlight(false);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.setHealth(20);
                player.setGameMode(GameMode.ADVENTURE);
                player.setSaturation(99999);
                player.setFoodLevel(24);
            }
            for (int i = 0; i < 8; i++)
            {
                if (player.getInventory().getItem(i) != null)
                {
                    player.getInventory().getItem(i).setDurability((short) 0);
                }
            }

            if (player.getInventory().getItem(8) == null)
            {
                ItemStack menuOpener = new ItemStack(Material.PAPER);
                ItemMeta itemData = menuOpener.getItemMeta();
                itemData.setLocalizedName("NoDrop");
                itemData.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.WHITE + "Menu" + ChatColor.GRAY + " " + PlayerInputTexts.Use.getIcon(player)));
                itemData.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Menu" + ChatColor.GRAY + " " + PlayerInputTexts.Use.getIcon(player));
                menuOpener.setItemMeta(itemData);
                player.getInventory().setItem(8, menuOpener);
            }

        }
        miniWorld.world.setEnableWeather(false);
        miniWorld.world.setTime("day");
        super.update();
    }

    public void createGUIMenu(Player player) {
        SGMenu myAwesomeMenu;
        if (api.isFloodgatePlayer(player.getUniqueId()))
        {
            myAwesomeMenu = LegacyConsole.spiGUI.create("Menu", 3);
        }
        else
        {
            myAwesomeMenu = LegacyConsole.spiGUI.create("Menu", 1);
        }
        ItemBuilder readyIcon = new ItemBuilder(new ItemStack(Material.LIME_WOOL, 1));
        readyIcon.name("&l&r&fReady");
        SGButton readyButton = new SGButton(readyIcon.build()).withListener((InventoryClickEvent event) -> {
            dontPlayBackSound.replace((Player) event.getWhoClicked(), true);
            readyMap.replace((Player) event.getWhoClicked(), !readyMap.get((Player) event.getWhoClicked()));
            event.getWhoClicked().closeInventory();
        });
        ItemBuilder chooseMapIcon = new ItemBuilder(new ItemStack(Material.LEGACY_EMPTY_MAP, 1));
        chooseMapIcon.name("&l&r&fVote for map");
        SGButton chooseMap = new SGButton(chooseMapIcon.build()).withListener((InventoryClickEvent event2) -> {
            dontPlayBackSound.replace((Player) event2.getWhoClicked(), true);
            createMapVoteGUI(player);
        });
        if (api.isFloodgatePlayer(player.getUniqueId())) // Bedrock
        {
            Bukkit.getLogger().info(api.getPlayer(player.getUniqueId()).getInputMode().toString());
            Bukkit.getLogger().info(api.getPlayer(player.getUniqueId()).getDeviceOs().toString());
            myAwesomeMenu.setButton(12, readyButton);
            myAwesomeMenu.setButton(14, chooseMap);
        }
        else // Java
        {
            myAwesomeMenu.setButton(3, readyButton);
            myAwesomeMenu.setButton(5, chooseMap);
        }

        player.getPlayer().openInventory(myAwesomeMenu.getInventory());

    }

    public void createMapVoteGUI(Player player) {
        SGMenu myAwesomeMenu;
        int rows;
        if (api.isFloodgatePlayer(player.getUniqueId()))
        {
            rows = (int) Math.round(Math.ceil(maps.size() / 3));
            if (rows <= 3)
            {
                rows = 3;
            }
        }
        else
        {
            rows = (int) Math.round(Math.ceil(maps.size() / 3));
        }
        myAwesomeMenu = LegacyConsole.spiGUI.create("Vote Map | Your Vote: " + mapVotes.get(player), rows);
        int i = 0;
        for (String map : maps.keySet())
        {
            ItemBuilder chooseMapIcon = new ItemBuilder(new ItemStack(Material.LEGACY_EMPTY_MAP, 1));
            chooseMapIcon.name("&l&r&f" + map);
            chooseMapIcon.lore("&l&r&fVotes - " + mapVoteNums.get(map));
            SGButton chooseMap = new SGButton(chooseMapIcon.build()).withListener((InventoryClickEvent event2) -> {
                if (event2.getWhoClicked() instanceof Player)
                {
                    Player player2 = (Player) event2.getWhoClicked();
                    mapVotes.replace(player2, map);
                    dontPlayBackSound.replace((Player) player2, true);
                    updateMapVoteNums();
                    createMapVoteGUI(player2);
                }

            });
            myAwesomeMenu.setButton(i, chooseMap);
            i++;
        }



        player.getPlayer().openInventory(myAwesomeMenu.getInventory());

    }

    // #region PLAYER EVENTS
    @Override
    public void onPlayerJoin(Player player) {
        readyMap.put(player, false);
        mapVotes.put(player, "");
        dontPlayBackSound.put(player, false);
        if (miniWorld.players.size() > 1 && !timerGameChange.timerIsRunning)
        {
            timerGameChange.reset();
        }
        spacerBar.addPlayer(player);
        infoBar.addPlayer(player);
    }

    @Override
    public void onPlayerLeave(Player player) {
        if (miniWorld.players.size() == 1)
        {
            timerGameChange.stop();
        }
        readyMap.remove(player);
        mapVotes.remove(player);
        dontPlayBackSound.remove(player);

        spacerBar.removePlayer(player);
        infoBar.removePlayer(player);
    }

    @Override
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if (!event.getBlock().getType().equals(Material.NOTE_BLOCK))
        {
            event.setCancelled(true);
        }
    }


    @Override
    public void onPlayerBlockDig(BlockDamageEvent event) {
        if (!event.getBlock().getType().equals(Material.NOTE_BLOCK))
        {
            event.setCancelled(true);
        }
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
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getItemMeta().hasLocalizedName())
        {
            if (event.getItemDrop().getItemStack().getItemMeta().getLocalizedName().equals("NoDrop"))
            {
                event.setCancelled(true);
            }
        }
    }


    @Override
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getClick().equals(ClickType.LEFT) || event.getClick().equals(ClickType.SHIFT_LEFT) || event.getClick().equals(ClickType.SHIFT_RIGHT) || event.getClick().equals(ClickType.NUMBER_KEY))
        {
            Sounds.playForPlayer(Sounds.CLICK, player, player.getLocation());
        }
        try
        {
            ItemStack item = event.getCurrentItem();
            if (item.getItemMeta().hasLocalizedName())
            {
                if (item.getItemMeta().getLocalizedName().equals("NoDrop"))
                {
                    event.setCancelled(true);
                }
            }

        }
        catch (Exception e)
        {
        }
    }

    @Override
    public void onPlayerInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getInventorySlots().size() == 1)
        {
            Sounds.playForPlayer(Sounds.CLICK, player, player.getLocation());
        }
    }


    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)))
        {
            if (event.getPlayer().getTargetBlock(null, 5) != null)
            {
                Block targetBlock = event.getPlayer().getTargetBlock(null, 5);
                if (targetBlock.getType().equals(Material.NOTE_BLOCK))
                {
                    Block noteBlock = targetBlock;
                    noteBlock.getState();

                    NoteBlock noteBlockData = (NoteBlock) noteBlock.getBlockData();
                    for (Player player : miniWorld.players)
                    {
                        player.playNote(noteBlock.getLocation(), noteBlockData.getInstrument(), noteBlockData.getNote());
                    }
                }
            }
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            if (event.getPlayer().getInventory().getHeldItemSlot() == 8)
            {
                createGUIMenu(event.getPlayer());
            }
        }

    }


    @Override
    public void onPlayerInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Sounds.playForPlayer(Sounds.CLICK, player, player.getLocation());
    }

    @Override
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (!dontPlayBackSound.get(player)) Sounds.playForPlayer(Sounds.BACK, player, player.getLocation());
        else dontPlayBackSound.replace(player, false);
    }

    @Override
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (PlayerInputType.getInputType(event.getPlayer()).equals(PlayerInputType.CONTROLLER) || PlayerInputType.getInputType(event.getPlayer()).equals(PlayerInputType.UNKNOWN))
        {
            if (!event.getPlayer().isSneaking())
            {
                createMapVoteGUI(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @Override
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {}
    // #endregion

    // #region ENTITY EVENTS

    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player)
        {
            event.setCancelled(true);
        }
        if (event.getEntity() instanceof Player)
        {
            event.setCancelled(true);
        }
    }


    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getWorld().getName().equals(miniWorld.world.getName()) && (event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting))
        {
            event.setCancelled(true);
        }
    }


    @Override
    public void onHangingEntityBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getEntity().getWorld().getName().equals(miniWorld.world.getName()) && (event.getEntity() instanceof Painting))
        {
            event.setCancelled(true);
        }
    }


    @Override
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
        }
    }
    // #endregion
}
