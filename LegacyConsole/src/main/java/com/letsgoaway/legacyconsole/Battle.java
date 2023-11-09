package com.letsgoaway.legacyconsole;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.letsgoaway.legacyconsole.mapdata.BattleMapData;
import com.letsgoaway.legacyconsole.mapdata.CavernSmall;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class Battle extends MiniGame {
    private String initWorldName = "";
    BossBar spacerBar;
    BossBar infoBar;
    private Team playerTeam;
    private int playersToJoin = 0;
    private int playersJoined = 0;
    private BattleMapData mapData;
    private Boolean gameStarted = false;
    public HashMap<Player, Location> spawnLocations = new HashMap<Player, Location>();
    public HashMap<Player, Boolean> inSpectatorMode = new HashMap<Player, Boolean>();
    public HashMap<Player, Bat> playerBats = new HashMap<Player, Bat>();
    public HashMap<Player, ItemStack> playerShieldItem = new HashMap<Player, ItemStack>();



    public TickTimer timeToStart;
    public TickTimer invulTimer;


    Battle(String worldName, int amountOfPlayersToJoin) {
        initWorldName = worldName;
        playersToJoin = amountOfPlayersToJoin;
        dontSendJoinMessage = true;
        if (worldName.equals("cavern_s"))
        {
            mapData = new CavernSmall();
        }
    }

    @Override
    public void create() {
        miniWorld.world.getCBWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        spacerBar = Bukkit.createBossBar(" ", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        infoBar = Bukkit.createBossBar("Starting", BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(LegacyConsole.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player playerWithOffhand = event.getPlayer();
                boolean playerIsInMiniWorld = false;
                for (Player player : miniWorld.players)
                {
                    if (player.getEntityId() == event.getPacket().getIntegers().read(0))
                    {
                        playerWithOffhand = player;
                        playerIsInMiniWorld = true;
                        break;
                    }
                }
                if (playerIsInMiniWorld)
                {
                    if (playerWithOffhand.equals(event.getPlayer()))
                    {
                        return;
                    }
                    ItemStack offhandItem = playerWithOffhand.getInventory().getItemInOffHand();
                    if (offhandItem.getType().equals(Material.SHIELD) && event.getPacket().getItemSlots().read(0).equals(EnumWrappers.ItemSlot.OFFHAND))
                    {
                        event.getPacket().getItemSlots().write(0, EnumWrappers.ItemSlot.OFFHAND);
                        if (offhandItem.getItemMeta().hasCustomModelData())
                        {
                            int id = offhandItem.getItemMeta().getCustomModelData();
                            if (id == 4)
                            {
                                event.getPacket().getItemModifier().write(0, new ItemStack(Material.AIR));
                            }
                            else if (id == 3)
                            {
                                event.getPacket().getItemModifier().write(0, new ItemStack(Material.FIREWORK_ROCKET, offhandItem.getAmount()));
                            }
                            else if (id == 2)
                            {
                                event.getPacket().getItemModifier().write(0, new ItemStack(Material.ARROW, offhandItem.getAmount()));
                            }
                            else if (id == 1)
                            {
                                event.getPacket().getItemModifier().write(0, new ItemStack(Material.TOTEM_OF_UNDYING, offhandItem.getAmount()));
                            }
                        }

                    }
                }

            }
        });
        // thanks https://www.baeldung.com/java-random-string
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

        playerTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(generatedString);
        playerTeam.setAllowFriendlyFire(true);
        playerTeam.setCanSeeFriendlyInvisibles(false);
        playerTeam.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
        playerTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
        playerTeam.setOption(Option.DEATH_MESSAGE_VISIBILITY, OptionStatus.ALWAYS);

        if (initWorldName.equals("cavern_s"))
        {
            miniWorld.world.getCBWorld();
        }
        miniWorld.world.setEnableWeather(false);
        miniWorld.world.setTime("day");
        miniWorld.world.setPVPMode(true);
        timeToStart = new TickTimer(10F, new Runnable() {
            public void run() {
                gameStart();
            }
        }, new Runnable() {
            public void run() {
                updateTopBarStartTimer();
            }
        });
        invulTimer = new TickTimer(16F, new Runnable() {
            public void run() {
                for (Player player : miniWorld.players)
                {
                    player.setInvulnerable(false);
                }
                infoBar.setTitle("Invulnerability has worn off! Fight!");
                Sounds.playForAll(Sounds.GRACEZERO, null, miniWorld);
            }
        }, new Runnable() {
            public void run() {
                updateTopBarInvulnerability();
            }
        });
        timeToStart.start();
        timeToStart.finishOnRoundedZero = true;
    }

    public void updateTopBarStartTimer() {
        if (timeToStart.roundedSecondsLeft != 0)
        {
            infoBar.setTitle("Time to start: " + timeToStart.roundedSecondsLeft + " seconds");
            if (timeToStart.roundedSecondsLeft <= 5)
            {
                Sounds.playForAll(Sounds.GAMETICK, null, miniWorld);
            }
        }
    }

    public void updateTopBarInvulnerability() {

        if (invulTimer.roundedSecondsLeft == 15)
        {
            infoBar.setTitle("Round start!");
        }
        else
        {
            if (invulTimer.roundedSecondsLeft != 0)
            {
                infoBar.setTitle("Invulnerability wears off in " + invulTimer.roundedSecondsLeft + " seconds!");
            }
            if (invulTimer.roundedSecondsLeft == 3 || invulTimer.roundedSecondsLeft == 2 || invulTimer.roundedSecondsLeft == 1)
            {
                Sounds.playForAll(Sounds.GRACE, null, miniWorld);
            }
        }
    }



    public void gameStart() {
        Sounds.playForAll(Sounds.GAMESTART, null, miniWorld);
        gameStarted = true;
        invulTimer.start();
        invulTimer.finishOnRoundedZero = true;
        Bukkit.getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
            public void run() {
                Sounds.playForAll(Sounds.MUSIC, null, miniWorld);
            }
        }, 20L);
    }

    public void onPlayerOffhandSwitch(PlayerSwapHandItemsEvent event) {
        if (event.getMainHandItem().getType().equals(Material.SHIELD))
        {
            event.setCancelled(true);
        }
    }

    @Override
    public void update() {
        for (Player player : miniWorld.players)
        {
            if (!gameStarted)
            {
                player.setWalkSpeed(0f);
                player.setSprinting(false);
            }
            else
            {
                player.setWalkSpeed(0.2f);
            }
            if (!player.getInventory().getItemInOffHand().getType().equals(Material.SHIELD))
            {
                playerShieldItem.replace(player, player.getInventory().getItemInOffHand().clone());
            }
            if (player.isHandRaised() || player.isBlocking())
            {
                if (player.getInventory().getItemInMainHand().getType().toString().contains("_SWORD"))
                {
                    ItemStack sword = player.getInventory().getItemInMainHand();
                    ItemMeta swordData = sword.getItemMeta();
                    swordData.setCustomModelData(1);
                    sword.setItemMeta(swordData);
                    player.setWalkSpeed(0.15f);
                }
            }
            else
            {
                player.setWalkSpeed(0.2f);
                if (player.getInventory().getItemInMainHand().getType().toString().contains("_SWORD"))
                {
                    ItemStack sword = player.getInventory().getItemInMainHand();
                    ItemMeta swordData = sword.getItemMeta();
                    swordData.setCustomModelData(null);
                    sword.setItemMeta(swordData);
                    ItemStack item = playerShieldItem.get(player);

                    if (!player.getInventory().getItemInOffHand().getType().equals(Material.SHIELD))
                    {
                        ItemStack shield;
                        if (item.getAmount() == 0)
                        {
                            shield = new ItemStack(Material.SHIELD, 1);
                        }
                        else
                        {
                            shield = new ItemStack(Material.SHIELD, item.getAmount());
                        }
                        ItemMeta data = shield.getItemMeta();
                        if (item.getType().equals(Material.AIR) || item.getAmount() == 0)
                        {
                            data.setCustomModelData(4);
                            data.setDisplayName("");
                        }
                        else if (item.getType().equals(Material.TOTEM_OF_UNDYING))
                        {
                            data.setCustomModelData(1);
                            data.setDisplayName("&l&r&eTotem of Undying");
                        }
                        else if (item.getType().equals(Material.ARROW))
                        {
                            data.setDisplayName("&l&r&fArrow");
                            data.setCustomModelData(2);
                        }
                        else if (item.getType().equals(Material.FIREWORK_ROCKET))
                        {
                            data.setCustomModelData(3);
                        }
                        shield.setItemMeta(data);
                        player.getInventory().setItemInOffHand(shield);
                    }
                }
                else
                {
                    player.getInventory().setItemInOffHand(playerShieldItem.get(player));
                }

            }
            miniWorld.world.setDifficulty(Difficulty.PEACEFUL);
        }

    }

    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {}


    @Override
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player)
        {
            Player player = (Player) event.getEntity();
            if (event.getFinalDamage() >= player.getHealth())
            {
                event.setCancelled(true);
                player.getInventory().setItemInOffHand(playerShieldItem.get(player));
                player.damage(event.getFinalDamage());
            }
        }
    }

    boolean noMoreDamage = false;


    @Override
    public void onPlayerDeath(PlayerDeathEvent event) {

        Sounds.playForAll(Sounds.DEATH, event.getEntity().getLocation(), miniWorld);
        Bukkit.getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
            public void run() {
                event.getEntity().spigot().respawn();
            }

        }, 20L);
    }


    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
        {
            if (event.getPlayer().getInventory().getItemInMainHand().getType().toString().contains("_SWORD"))
            {
                ItemStack sword = event.getPlayer().getInventory().getItemInMainHand();
                ItemMeta swordData = sword.getItemMeta();
                swordData.setCustomModelData(null);
                sword.setItemMeta(swordData);
            }
        }


    }

    @Override
    public void onPlayerInventoryOpen(InventoryOpenEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(LegacyConsole.plugin, new Runnable() {
            public void run() {
                TextComponent takeEverything = new TextComponent("\uE24F");
                takeEverything.setColor(ChatColor.WHITE);
                LegacyConsole.titleHandler.setPlayerInventoryTitle((Player) event.getPlayer(), new TextComponent("Chest                                      "), takeEverything);
            }
        }, 1L);

    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getY() > event.getFrom().getY() || event.getTo().getX() > event.getFrom().getX() || event.getTo().getZ() > event.getFrom().getZ())
        {
            if (!gameStarted)
            {
                event.getTo().setX(spawnLocations.get(event.getPlayer()).getX() + 0.5);
                event.getTo().setY(spawnLocations.get(event.getPlayer()).getY());
                event.getTo().setZ(spawnLocations.get(event.getPlayer()).getZ() + 0.5);
            }
        }
    }

    @Override
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getSlot() == 40 && event.getCurrentItem().getType().equals(Material.SHIELD))
        {
            event.setCancelled(true);
        }
        else if (event.getSlot() == -999 && event.getInventory().getType().equals(InventoryType.CHEST) && event.getCursor().getAmount() == 0)
        {
            HashMap<Integer, ItemStack> itemsNotTaken = new HashMap<Integer, ItemStack>();
            int curSlot2 = 0;
            for (ItemStack item : event.getInventory().getStorageContents()) // theres definetly a better way to do this but im lazy atm!!
            {
                if (item != null)
                {
                    HashMap<Integer, ItemStack> items = event.getWhoClicked().getInventory().addItem(item);
                    if (items.size() != 0)
                    {
                        for (int itemSlot : items.keySet())
                            itemsNotTaken.put(curSlot2, items.get(itemSlot));
                    }
                }
                curSlot2++;
            }
            int curSlot = 0;
            for (ItemStack itemSlot : event.getInventory().getStorageContents())
            {
                if (!itemsNotTaken.containsKey(curSlot)) // if item was taken
                {
                    event.getInventory().setItem(curSlot, new ItemStack(Material.AIR, 1));
                }
                curSlot++;
            }
            Player player = (Player) event.getWhoClicked();
            player.updateInventory();
            Sounds.playForPlayer(Sounds.CLICK, player, player.getLocation());
        }
    }

    @Override
    public void onPlayerJoin(Player player) {
        playerBats.put(player, (Bat) miniWorld.world.getCBWorld().spawnEntity(new Location(miniWorld.world.getCBWorld(), 0, 9, 0), EntityType.BAT));
        player.setInvulnerable(true);
        playerTeam.addEntry(player.getName());
        playerShieldItem.put(player, null);

        if (mapData != null)
        {
            player.teleport(mapData.getSpawnLocations(this.miniWorld.world.getCBWorld())[playersJoined]);
            spawnLocations.put(player, mapData.getSpawnLocations(this.miniWorld.world.getCBWorld())[playersJoined]);
            player.teleport(new Location(miniWorld.world.getCBWorld(), player.getLocation().getX() + 0.5, player.getLocation().getY(), player.getLocation().getZ() + 0.5));
        }
        playersJoined++;


        player.getInventory().clear();
        spacerBar.addPlayer(player);
        infoBar.addPlayer(player);
    }

    @Override
    public void onPlayerLeave(Player player) {
        playerTeam.removeEntry(player.getName());
        playerShieldItem.remove(player);
        spawnLocations.remove(player);
        spacerBar.removePlayer(player);
        infoBar.removePlayer(player);
    }


    @Override
    public MultiverseWorld createWorld(String uuidAsString) {
        return WorldUtils.createRuntimeWorld(initWorldName, "battle_" + uuidAsString, null);
    }

}
