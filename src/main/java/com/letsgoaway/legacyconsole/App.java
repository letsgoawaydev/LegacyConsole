package com.letsgoaway.legacyconsole;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerDigType;

/**
 * Hello world!
 *
 */
public class App extends JavaPlugin {
    static App plugin;
    static MiniGameType mode = MiniGameType.BATTLE;
    static int tickCounter = 0;

    @Override
    public void onEnable() {
        plugin = this;
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                update();
            }
        }, 0L, 1L);
        Battle.create();
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
            public void onPacketSending(PacketEvent event) {
                if (event.getNetworkMarker().getSide().equals(ConnectionSide.SERVER_SIDE))
                {
                    PacketContainer packet = event.getPacket();
                    World world = event.getPlayer().getWorld();
                    String soundName = packet.getSoundEffects().read(0).name();
                    double x = (packet.getIntegers().read(0) / 8.0);
                    double y = (packet.getIntegers().read(1) / 8.0);
                    double z = (packet.getIntegers().read(2) / 8.0);
                    Location loc = new Location(world, x, y, z);
                    if (soundName.endsWith("_STEP") || soundName.endsWith("_SPLASH") || soundName.endsWith("_SWIM"))
                    {
                        Player closest = null;
                        double bestDistance = Double.MAX_VALUE;
                        // Find the player closest to the sound
                        for (Player player : world.getPlayers())
                        {
                            double distance = player.getLocation().distance(loc);
                            if (distance < bestDistance)
                            {
                                bestDistance = distance;
                                closest = player;
                            }
                        }
                        PlayerListener.onFootstep(event, closest);
                    }
                }
            }
        });
    }

    public void update() {
        // for (Player player : this.getServer().getOnlinePlayers()) {
        // }
        tickCounter++;
        switch (mode) {
            case BATTLE:
                Battle.update();
            case TUMBLE:
                Tumble.update();
            case GLIDE:
                Glide.update();
        }
        lobbyUpdate();
    }

    public void lobbyUpdate() {
        for (Player player : this.getServer().getOnlinePlayers())
        {
            if (player.getWorld().getName().equals("lobby"))
            {
                player.getWorld().setPVP(false);
                player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                player.setSaturation(999);
                player.setFoodLevel(20);
                player.setFallDistance(0);
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }
}
