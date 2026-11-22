package com.epicplayera10.potionexpansion.utils;

import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.Vein;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.List;

public class XRayUtil {
    @ParametersAreNonnullByDefault
    public static void showPathsToMaterial(Player player, Material material, Color color, int r) {
        Location start = player.getLocation().clone().add(0, 1, 0);
        Block startBlock = player.getLocation().getBlock();

        List<Block> veinsCache = new ArrayList<>();

        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Block block = startBlock.getRelative(x, y, z);

                    if (block.getType() == material && !veinsCache.contains(block)) {
                        List<Block> vein = Vein.find(block, 30);
                        veinsCache.addAll(vein);

                        drawLine(color, start, block.getLocation().clone().add(0.5, 0.5, 0.5), 0.3);
                    }
                }
            }
        }
    }

    @ParametersAreNonnullByDefault
    private static void drawLine(Color color, Location point1, Location point2, double space) {
        World world = point1.getWorld();
        assert point2.getWorld().equals(world);

        double distance = point1.distance(point2);
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        for (double length = 0; length < distance; p1.add(vector)) {
            world.spawnParticle(Particle.DUST, p1.getX(), p1.getY(), p1.getZ(), 1, new Particle.DustOptions(color, 1f));
            length += space;
        }
    }
}
