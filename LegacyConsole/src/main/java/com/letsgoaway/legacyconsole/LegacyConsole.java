package com.letsgoaway.legacyconsole;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import com.letsgoaway.legacyconsole.commands.HubCommand;
import com.samjakob.spigui.SpiGUI;

/**
 * Hello world!
 *
 */
public class LegacyConsole extends JavaPlugin {
	public static LegacyConsole plugin;
	public static SpiGUI spiGUI;
	static int tickCounter = 0;

	public static Map<String, MiniWorld> hubs = new HashMap<String, MiniWorld>();
	public static Map<String, MiniWorld> battle_lobby = new HashMap<String, MiniWorld>();
	public static Map<String, MiniWorld> battle = new HashMap<String, MiniWorld>();



	@Override
	public void onEnable() {
		spiGUI = new SpiGUI(this);
		LegacyConsole.plugin = this;
		WorldUtils.init();
		this.getCommand("hub").setExecutor(new HubCommand());
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		hubs.put("hub1", new MiniWorld(WorldUtils.createRuntimeWorld("tut_hub", "hub1"), new Hub()));
		hubs.put("hub2", new MiniWorld(WorldUtils.createRuntimeWorld("tut_hub", "hub2"), new Hub()));
		hubs.put("hub3", new MiniWorld(WorldUtils.createRuntimeWorld("tut_hub", "hub3"), new Hub()));
		hubs.put("hub4", new MiniWorld(WorldUtils.createRuntimeWorld("tut_hub", "hub4"), new Hub()));
		battle_lobby.put("temp", new MiniWorld(WorldUtils.createRuntimeWorld("lobby_backup", "temp"), new Lobby(GameTypes.BATTLE)));
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				update();
			}

		}, 0L, 1L);

	}

	public void update() {
		// for (Player player : this.getServer().getOnlinePlayers()) {
		// }
		tickCounter++;
		for (MiniWorld miniWorld : hubs.values())
		{
			miniWorld.update();
		}
		for (MiniWorld miniWorld : battle_lobby.values())
		{
			miniWorld.update();
		}
	}


	@Override
	public void onDisable() {
		for (MiniWorld miniW : hubs.values())
		{
			WorldUtils.deleteWorld(miniW.world.getName());
		}
		for (MiniWorld miniW : battle_lobby.values())
		{
			WorldUtils.deleteWorld(miniW.world.getName());
		}
	}
}
