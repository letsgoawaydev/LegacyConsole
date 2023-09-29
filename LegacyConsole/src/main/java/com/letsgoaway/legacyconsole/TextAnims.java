package com.letsgoaway.legacyconsole;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TextAnims {
	public static String[] chestRefill =
	{
			"\uE030",
			"\uE030",
			"\uE031",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE032",
			"\uE031",
			"\uE030",
			"\uE030",
			"\uE030",
			"\uE030",
			"\uE030",
			"\uE030"
	};
	// for some reason 1.15 and older is like stupid and dumb so we use these
	// instead
	public static String[] chestRefillLegacy =
	{
			"≡", "≡", "±", "≥", "≥", "≥", "≥", "≥", "≥", "≥", "≥", "≥", "≥", "≥", "±", "≡", "≡", "≡", "≡", "≡", "≡"
	};

	public static String getFrame(String[] anim, int frame) {
		return anim[frame % anim.length];
	}

	public static List<String> animsPlaying = new ArrayList<String>();

	public static boolean isAnAnimPlayingFor(Player player) {
		if (animsPlaying.contains(player.getName())) return true;
		else return false;
	}

	public static void playAnim(String[] anim, AnimLocation location, Player player, float speed, Sounds sound) {
		if (sound != null)
		{
			Sounds.playForPlayer(sound, player, player.getLocation());
		}
		switch (location) {
			case BOSSBAR:
				BossBar bossbar = Bukkit.createBossBar(TextAnims.getFrame(anim, 0), BarColor.WHITE, BarStyle.SOLID, BarFlag.DARKEN_SKY);
				bossbar.addPlayer(player);
				if (!animsPlaying.contains(player.getName())) animsPlaying.add(player.getName());
				new BukkitRunnable() {
					float updateFrame = 0;

					public void run() {
						if (Math.round(updateFrame) < (anim.length + 1))
						{
							bossbar.setTitle(getFrame(anim, Math.round(updateFrame)));
							updateFrame += 1 * speed;
						}
						else
						{
							animsPlaying.remove(player.getName());
							bossbar.removeAll();
							cancel();
						}
					}
				}.runTaskTimer(LegacyConsole.plugin, 0, 1);
			case TITLE:
				if (!animsPlaying.contains(player.getName())) animsPlaying.add(player.getName());
				new BukkitRunnable() {
					float updateFrame = 0;

					public void run() {
						if (Math.round(updateFrame) < (anim.length + 1))
						{
							player.sendTitle(getFrame(anim, Math.round(updateFrame)), "", 0, 20, 0);
							updateFrame += 1 * speed;
						}
						else
						{
							animsPlaying.remove(player.getName());
							cancel();
						}
					}
				}.runTaskTimer(LegacyConsole.plugin, 0, 1);
			case SUBTITLE:
				if (!animsPlaying.contains(player.getName())) animsPlaying.add(player.getName());
				new BukkitRunnable() {
					float updateFrame = 0;

					public void run() {
						if (Math.round(updateFrame) < (anim.length + 1))
						{
							player.sendTitle("", getFrame(anim, Math.round(updateFrame)), 0, 20, 0);
							updateFrame += 1 * speed;
						}
						else
						{
							animsPlaying.remove(player.getName());
							cancel();
						}
					}
				}.runTaskTimer(LegacyConsole.plugin, 0, 1);
		}
	}
}
