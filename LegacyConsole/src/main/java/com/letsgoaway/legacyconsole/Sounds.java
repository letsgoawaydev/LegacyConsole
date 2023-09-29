package com.letsgoaway.legacyconsole;

import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public enum Sounds {
	GRACE("battle.grace"), GRACEZERO("battle.gracezero"), CHEST("battle.chest"), DEATH("battle.death"), MUSIC("music.dragon"), SQUEAK("entity.bat.ambient", 0.5f),
	MENU_MUSIC("music.menu", 1, SoundCategory.MASTER),;

	private String id;
	private float volume;
	private SoundCategory category;

	private Sounds(final String id, final float volume, final SoundCategory category) {
		this.id = id;
		this.volume = volume;
		this.category = category;
	}

	private Sounds(final String id, final float volume) {
		this.id = id;
		this.volume = volume;
		this.category = SoundCategory.MASTER;
	}

	private Sounds(final String id) {
		this.id = id;
		this.volume = 1.00f;
		this.category = SoundCategory.MASTER;
	}

	public static void playForAll(Sounds sound, World world, org.bukkit.Location position) {
		Boolean noPos = false;
		if (position == null)
		{
			noPos = true;
		}
		for (Player player : Bukkit.getServer().getOnlinePlayers())
		{
			{
				if (noPos)
				{
					position = player.getLocation();
				}
				if (player.getWorld().equals(world))
				{
					player.playSound(position, sound.id, sound.category, sound.volume, 1);
				}
			}
		}
	}

	public static void playForPlayer(Sounds sound, Player player, org.bukkit.Location position) {
		if (position == null)
		{
			position = player.getLocation();
		}
		player.playSound(position, sound.id, sound.category, sound.volume, 1);
	}
}