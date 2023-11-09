package com.letsgoaway.legacyconsole;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class WorldUtils {
	static MultiverseCore core;
	public static MVWorldManager worldManager;

	public static Boolean isWorldLoaded(String name) {
		for (World world : Bukkit.getWorlds())
		{
			if (world.getName().equals(name)) return true;
		}
		return false;
	}

	public static void init() {
		core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		worldManager = core.getMVWorldManager();
	}

	public static Boolean loadWorld(String name) {
		worldManager.addWorld(name, Environment.NORMAL, null, WorldType.NORMAL, true, null);
		return worldManager.loadWorld(name);
	}


	public static Boolean unloadWorld(String name) {
		return worldManager.unloadWorld(name, false);

	}

	public static Boolean deleteWorld(String name) {
		return worldManager.deleteWorld(name);
	}


	// creates world that will only exist at runtime
	public static MultiverseWorld createRuntimeWorld(String originalWorld, String newWorldName, Environment dimension) {
		if (worldManager.getMVWorld(originalWorld) == null)
		{
			if (dimension == null)
			{
				dimension = Environment.NORMAL;
			}
			worldManager.addWorld(originalWorld, dimension, null, WorldType.NORMAL, false, null);
		}
		worldManager.cloneWorld(originalWorld, newWorldName);
		worldManager.loadWorld(newWorldName);
		MultiverseWorld world = worldManager.getMVWorld(newWorldName);
		world.setKeepSpawnInMemory(false);
		world.setAutoLoad(false);
		world.setAllowAnimalSpawn(false);
		return worldManager.getMVWorld(newWorldName);
	}

}
