package de.potera.teamhardcore.others.mines.enchantments;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class EnchantmentEffects {

    private static final Random RANDOM = new Random();
    private static final Set<Material> VEIN_BLOCKS = new HashSet<>(Arrays.asList(
            Material.COAL_ORE, Material.IRON_ORE, Material.DIAMOND_ORE, Material.REDSTONE_ORE, Material.LAPIS_ORE,
            Material.EMERALD_ORE, Material.GOLD_ORE, Material.QUARTZ_ORE));

    private static final double[] TWEAKED_LEVEL1 = new double[]{1.0D, 0.7D, 0.6D};
    private static final double[] TWEAKED_LEVEL2 = new double[]{2.0D, 0.6D, 0.4D};
    private static final double[] TWEAKED_LEVEL3 = new double[]{3.0D, 0.4D, 0.4D};
    private static final double[] TWEAKED_LEVEL4 = new double[]{3.0D, 0.6D, 0.5D};

    public static Set<Location> createExplosion(Location middle, int power, double distanceLastBlocks, double maxPercentLastBlocks) {
        Set<Location> explodedBlocks = new HashSet<>();
        Set<Location> deniedBlocks = new HashSet<>();

        double radiusX = power;
        double radiusY = power;
        double radiusZ = power;

        radiusX += 0.5D;
        radiusY += 0.5D;
        radiusZ += 0.5D;

        double invRadiusX = 1.0D / radiusX;
        double invRadiusY = 1.0D / radiusY;
        double invRadiusZ = 1.0D / radiusZ;

        int ceilRadiusX = (int) Math.ceil(radiusX);
        int ceilRadiusY = (int) Math.ceil(radiusY);
        int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0.0D;
        int x;
        label54:
        for (x = 0; x <= ceilRadiusX; x++) {
            double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextYn = 0.0D;
            int y;
            label53:
            for (y = 0; y <= ceilRadiusY; y++) {
                double yn = nextYn;
                nextYn = (y + 1) * invRadiusY;
                double nextZn = 0.0D;
                for (int z = 0; z <= ceilRadiusZ; z++) {
                    double zn = nextZn;
                    nextZn = (z + 1) * invRadiusZ;

                    double distanceSq = lengthSq(xn, yn, zn);
                    if (distanceSq > 1.0D) {
                        if (z == 0) {
                            if (y == 0) {
                                break label54;
                            }

                            break label53;
                        }
                        break;
                    }
                    for (int expX = 1; expX <= 2; expX++) {
                        for (int expY = 1; expY <= 2; expY++) {
                            for (int expZ = 1; expZ <= 2; expZ++) {
                                Location loc = middle.clone().add(x * Math.pow(-1.0D, expX), y * Math.pow(-1.0D, expY),
                                        z * Math.pow(-1.0D, expZ));

                                if (loc.getBlock().getType() == Material.BEDROCK) {
                                    deniedBlocks.add(loc);
                                } else {

                                    BlockIterator iter = new BlockIterator(middle.getWorld(), loc.toVector(),
                                            middle.toVector(), 0.0D, 1);

                                    if (iter.hasNext()) {
                                        Location locBefore = iter.next().getLocation();
                                        if (!deniedBlocks.contains(locBefore)) {
                                            if (distanceSq > distanceLastBlocks) {
                                                double chance = RANDOM.nextDouble() * maxPercentLastBlocks;
                                                if (RANDOM.nextDouble() > chance) {
                                                    deniedBlocks.add(loc);
                                                } else {
                                                    explodedBlocks.add(loc);
                                                }
                                            } else {
                                                explodedBlocks.add(loc);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        explodedBlocks.remove(middle);
        return explodedBlocks;
    }

    public static double[] getTweakedExplosionSettings(int enchantmentLevel) {
        switch (enchantmentLevel) {
            case 1:
                return TWEAKED_LEVEL1;
            case 2:
                return TWEAKED_LEVEL2;
            case 3:
                return TWEAKED_LEVEL3;
            case 4:
                return TWEAKED_LEVEL4;
        }
        return TWEAKED_LEVEL1;
    }

    public static Set<Location> getVeinBlocks(Location middle, int power) {
        Set<Location> veinLocs = new HashSet<>();
        Block baseBlock = middle.getBlock();
        int range = power * 2;
        List<Block> recent = new ArrayList<>(Collections.singletonList(baseBlock));
        List<Block> buffer = new ArrayList<>();

        while (!recent.isEmpty()) {
            for (Block current : recent) {
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            if (x != 0 || y != 0 || z != 0) {

                                Block rel = current.getLocation().clone().add(x, y, z).getBlock();
                                if (!veinLocs.contains(
                                        rel.getLocation()) && rel.getType() == baseBlock.getType() && VEIN_BLOCKS.contains(
                                        rel.getType()) && isInsideRange(middle, rel.getLocation(), range)) {
                                    buffer.add(rel);
                                }
                            }
                        }
                    }
                }
            }
            recent.clear();
            recent.addAll(buffer);
            buffer.forEach(b -> veinLocs.add(b.getLocation()));
            buffer.clear();
        }

        return veinLocs;
    }

    private static boolean isInsideRange(Location baseLoc, Location testLoc, int range) {
        int minX = baseLoc.getBlockX() - range;
        int minY = baseLoc.getBlockY() - range;
        int minZ = baseLoc.getBlockZ() - range;
        int maxX = baseLoc.getBlockX() + range;
        int maxY = baseLoc.getBlockY() + range;
        int maxZ = baseLoc.getBlockZ() + range;
        return (minX <= testLoc.getBlockX() && maxX >= testLoc.getBlockX() && minY <= testLoc.getBlockY() && maxY >= testLoc.getBlockY() && minZ <= testLoc
                .getBlockZ() && maxZ >= testLoc.getBlockZ());
    }

    private static double lengthSq(double x, double y, double z) {
        return x * x + y * y + z * z;
    }


}
