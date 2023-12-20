package me.stephenminer.oreRegeneration.DynamicRegion;

import me.stephenminer.oreRegeneration.OreRegeneration;
import me.stephenminer.oreRegeneration.Regions.DynamicRegion;
import me.stephenminer.oreRegeneration.Regions.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.*;

public class Create {
    private OreRegeneration plugin;
    public Create(OreRegeneration plugin){
        this.plugin = plugin;
    }
    private Location loc1;
    private Location loc2;
    private String name;

    public static HashMap<String, HashMap<Material, Integer>> countMap = new HashMap<String, HashMap<Material, Integer>>();

    public Block getRandomBlock(List<Block> blocks){
        Random r = new Random();
        return blocks.get(r.nextInt(blocks.size()));
    }
    List<Block> tempList = new ArrayList<>();
    public Block getRandomBlock(List<Block> blocks, List<Material> filter){
        Random r = new Random();
        Block b = blocks.get(r.nextInt(blocks.size()));
        if (!filter.contains(b.getType()))
            return getRandomBlock(blocks, filter);
        return b;
    }

    public void createRegion(){
        plugin.DynamicRegionFile.getConfig().set("regions." + name + ".pos1", plugin.fromBlockLoc(loc1));
        plugin.DynamicRegionFile.getConfig().set("regions." + name + ".pos2", plugin.fromBlockLoc(loc2));
        plugin.DynamicRegionFile.saveConfig();
      //  plugin.DynamicRegionFile.reloadConfig();
        //plugin.DynamicRegionFile.getConfig().createSection("regions." + name);
        plugin.regions.add(new DynamicRegion(plugin, name));
    }



    public static Set<Block> getOutline(String name, OreRegeneration plugin){
        if (plugin.DynamicRegionFile.getConfig().contains("regions." + name)){
            Location loc1 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + name + ".loc1");
            Location loc2 = (Location) plugin.DynamicRegionFile.getConfig().get("regions." + name + ".loc2");
            Set<Block> blockSet = new HashSet<>();
            World world = loc1.getWorld();
            org.bukkit.util.Vector fp = loc1.toVector();
            org.bukkit.util.Vector sp = loc2.toVector();
            org.bukkit.util.Vector max = org.bukkit.util.Vector.getMaximum(fp,sp);
            org.bukkit.util.Vector min = Vector.getMinimum(fp,sp);

            for (int y = min.getBlockY(); y <= max.getBlockY(); y++){
                for (int x = min.getBlockX() - 1; x <= max.getBlockX() + 1; x++){
                    blockSet.add(world.getBlockAt(x,y, min.getBlockZ() - 1));
                    blockSet.add(world.getBlockAt(x,y,max.getBlockZ() + 1));
                }
                for (int z = min.getBlockZ() - 1; z <= max.getBlockZ() + 1; z++){
                    blockSet.add(world.getBlockAt(min.getBlockX() - 1,y, z));
                    blockSet.add(world.getBlockAt(max.getBlockX() + 1,y,z));
                }
            }
            return blockSet;
        }
        return null;
    }

    public Location getLoc1(){
        return loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public void setLoc2(Location loc2){
        this.loc2 = loc2;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getMaxBlocks(String region, Material mat){
        return plugin.DynamicRegionFile.getConfig().getInt("regions." + region + ".canBreak." + mat.toString() + ".maxBlocks");
    }
    public int getCurrentBlocks(String region, Material mat){
        return countMap.get(region).get(mat);
    }

}
