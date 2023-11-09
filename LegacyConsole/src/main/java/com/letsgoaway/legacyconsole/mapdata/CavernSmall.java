package com.letsgoaway.legacyconsole.mapdata;

import org.bukkit.Location;
import org.bukkit.World;

import com.viaversion.viaversion.api.minecraft.Vector3f;

public class CavernSmall extends BattleMapData {

    public CavernSmall() {};

    @Override
    public Location[] getSpawnLocations(World world) {
        Location[] pos =
        {
                new Location(world, -222, 63, 273, 0, 0),
                new Location(world, -212, 63, 276, 45, 0),
                new Location(world, -209, 63, 286, 90, 0),
                new Location(world, -212, 63, 296, 135, 0),
                new Location(world, -221, 63, 300, 180, 0),
                new Location(world, -232, 63, 296, -135, 0),
                new Location(world, -235, 63, 286, -90, 0),
                new Location(world, -232, 63, 276, -45, 0),
        };
        return pos;
    }
}

