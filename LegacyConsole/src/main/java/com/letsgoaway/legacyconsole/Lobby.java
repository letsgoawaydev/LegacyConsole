package com.letsgoaway.legacyconsole;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.NoteBlock;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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


    public Lobby(GameTypes type) {
        super();
        this.type = type;
    }

    public void create() {
        if (type.equals(GameTypes.BATTLE))
        {
            maps.put("Cove", "cove");
            maps.put("Crucible", "crucible");
            maps.put("Cavern", "cavern");
            mapVoteNums.put("Cove", 0);
            mapVoteNums.put("Crucible", 0);
            mapVoteNums.put("Cavern", 0);
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

    public void updateMapVoteNums() {
        if (type.equals(GameTypes.BATTLE))
        {

            mapVoteNums.replace("Cove", 0);
            mapVoteNums.replace("Crucible", 0);
            mapVoteNums.replace("Cavern", 0);
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
                player.setFallDistance(0);
                player.setFireTicks(0);
                player.setAllowFlight(false);
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                player.removePotionEffect(PotionEffectType.POISON);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.setHealth(20);
                player.setNoDamageTicks(999);
                player.setGameMode(GameMode.ADVENTURE);
                player.setSaturation(99999);
                player.setFoodLevel(24);
            }


            ItemStack menuOpener = new ItemStack(Material.PAPER);
            ItemMeta itemData = menuOpener.getItemMeta();
            itemData.setLocalizedName("Menu");
            itemData.setLore(Arrays.asList(ChatColor.RESET + "" + ChatColor.WHITE + "Menu" + ChatColor.GRAY + " [Use]"));
            itemData.setDisplayName(ChatColor.RESET + "" + ChatColor.WHITE + "Menu" + ChatColor.GRAY + " [Use]");
            itemData.setUnbreakable(true);
            menuOpener.setItemMeta(itemData);
            player.getInventory().setItem(8, menuOpener);

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
        ItemBuilder readyIcon = new ItemBuilder(new ItemStack(Material.WOOL, 1, (short) 13));
        readyIcon.name("&l&r&fReady");
        SGButton readyButton = new SGButton(readyIcon.build()).withListener((InventoryClickEvent event) -> {
            readyMap.replace((Player) event.getWhoClicked(), !readyMap.get((Player) event.getWhoClicked()));
            event.getWhoClicked().closeInventory();
        });
        ItemBuilder chooseMapIcon = new ItemBuilder(new ItemStack(Material.EMPTY_MAP, 1));
        chooseMapIcon.name("&l&r&fVote for map");
        SGButton chooseMap = new SGButton(chooseMapIcon.build()).withListener((InventoryClickEvent event2) -> {
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
            ItemBuilder chooseMapIcon = new ItemBuilder(new ItemStack(Material.EMPTY_MAP, 1));
            chooseMapIcon.name("&l&r&f" + map);
            chooseMapIcon.lore("&l&r&fVotes - " + mapVoteNums.get(map));
            SGButton chooseMap = new SGButton(chooseMapIcon.build()).withListener((InventoryClickEvent event2) -> {
                if (event2.getWhoClicked() instanceof Player)
                {
                    mapVotes.replace((Player) event2.getWhoClicked(), map);
                    Player player2 = (Player) event2.getWhoClicked();
                    createMapVoteGUI(player2);
                }

            });
            myAwesomeMenu.setButton(i, chooseMap);
            i++;
        }



        player.getPlayer().openInventory(myAwesomeMenu.getInventory());

    }

    @Override
    public void onPlayerJoin(Player player) {
        readyMap.put(player, false);
        mapVotes.put(player, "");

        spacerBar.addPlayer(player);
        infoBar.addPlayer(player);
    }

    public void onPlayerLeave(Player player) {
        readyMap.remove(player);
        mapVotes.remove(player);

        spacerBar.removePlayer(player);
        infoBar.removePlayer(player);
    }

    @EventHandler
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (gameHasPlayer(event.getPlayer()) && !event.getBlock().getType().equals(Material.NOTE_BLOCK))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @Override
    public void onBlockDig(BlockDamageEvent event) {
        if (gameHasPlayer(event.getPlayer()) && !event.getBlock().getType().equals(Material.NOTE_BLOCK))
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
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            if (gameHasPlayer(player))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getWorld().getName().equals(miniWorld.world.getName()) && (event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @Override
    public void onHangingEntityBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getEntity().getWorld().getName().equals(miniWorld.world.getName()) && (event.getEntity() instanceof Painting))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    @Override
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            event.setCancelled(true);
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
    public void onItemDrop(PlayerDropItemEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {
            if (event.getItemDrop().getItemStack().getItemMeta().isUnbreakable())
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player)
        {
            Player player = (Player) event.getWhoClicked();
            if (gameHasPlayer(player))
            {
                if (event.getCurrentItem().getItemMeta().isUnbreakable())
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (gameHasPlayer(event.getPlayer()) && event.getAction().equals(Action.LEFT_CLICK_BLOCK) && event.getPlayer().getGameMode().equals(GameMode.ADVENTURE))
        {
            if (event.getClickedBlock() instanceof NoteBlock)
            {
                NoteBlock noteBlock = (NoteBlock) event.getClickedBlock();
                noteBlock.play();
            }
        }
        if (gameHasPlayer(event.getPlayer()) && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
        {
            if (event.getPlayer().getInventory().getHeldItemSlot() == 8)
            {
                createGUIMenu(event.getPlayer());
            }
        }

    }


    @EventHandler
    @Override
    public void onInventoryClose(InventoryCloseEvent event) {

    }



    @Override
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {


    }

    @EventHandler
    @Override
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (gameHasPlayer(event.getPlayer()))
        {

        }
    }


}
