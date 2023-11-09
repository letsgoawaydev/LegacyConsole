package com.letsgoaway.legacyconsole;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.comphenix.protocol.ProtocolLibrary;
import com.letsgoaway.legacyconsole.commands.HubCommand;
import com.letsgoaway.legacyconsole.commands.SetResourceProxyCommand;
import com.letsgoaway.legacyconsole.resourcepack.ResourcePackServer;
import com.samjakob.spigui.SpiGUI;

/**
 * Hello world!
 *
 */
public class LegacyConsole extends JavaPlugin implements PluginMessageListener {
	public static LegacyConsole plugin;
	public static SpiGUI spiGUI;
	static int tickCounter = 0;

	public static ArrayList<MiniWorld> hubs = new ArrayList<MiniWorld>();
	public static ArrayList<MiniWorld> battle_lobby = new ArrayList<MiniWorld>();
	public static ArrayList<MiniWorld> battle = new ArrayList<MiniWorld>();
	public static ResourcePackServer resourcePackServer;
	public static TitleHandler titleHandler;

	@Override
	public void onEnable() {
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		spiGUI = new SpiGUI(this);
		LegacyConsole.plugin = this;
		WorldUtils.init();
		titleHandler = new TitleHandler(this, ProtocolLibrary.getProtocolManager());
		titleHandler.registerPacketListeners();
		this.getCommand("hub").setExecutor(new HubCommand());
		this.getCommand("setRPProxy").setExecutor(new SetResourceProxyCommand());
		Bukkit.getPluginManager().registerEvents(new MiscListener(), this);
		hubs.add(new MiniWorld(new Hub()));
		hubs.add(new MiniWorld(new Hub()));
		hubs.add(new MiniWorld(new Hub()));
		hubs.add(new MiniWorld(new Hub()));
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				update();
			}

		}, 0L, 1L);
		try
		{
			resourcePackServer = new ResourcePackServer();
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {

		if (!channel.equals("BungeeCord"))
		{
			return;
		}

	}

	public void update() {
		// for (Player player : this.getServer().getOnlinePlayers()) {
		// }
		tickCounter++;
		TickTimer.update();
		try
		{
			for (MiniWorld miniWorld : hubs)
			{
				miniWorld.update();
			}
			for (MiniWorld miniWorld : battle_lobby)
			{
				if (miniWorld.gameIsDone)
				{
					battle_lobby.remove(miniWorld);
				}
				else
				{
					miniWorld.update();
				}
			}
			for (MiniWorld miniWorld : battle)
			{
				if (miniWorld.gameIsDone)
				{
					battle.remove(miniWorld);
				}
				else
				{
					miniWorld.update();
				}
			}
		}
		catch (Exception e)
		{
			// TODO handle
		}

	}


	@Override
	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers())
		{
			player.kickPlayer("Server closed");
		}
		this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
		this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
		for (MiniWorld miniW : hubs)
		{
			WorldUtils.deleteWorld(miniW.world.getName());
		}
		for (MiniWorld miniW : battle_lobby)
		{
			WorldUtils.deleteWorld(miniW.world.getName());
		}
		for (MiniWorld miniW : battle)
		{
			WorldUtils.deleteWorld(miniW.world.getName());
		}
	}
}
